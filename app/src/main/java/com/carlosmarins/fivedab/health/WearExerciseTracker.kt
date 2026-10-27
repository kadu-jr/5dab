package com.carlosmarins.fivedab.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.concurrent.futures.await
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

data class HeartRateSamplePoint(
    val timestampEpochMs: Long,
    val bpm: Int
)

data class LiveBiometricsState(
    val currentHeartRateBpm: Int = 0,
    val avgHeartRateBpm: Int = 0,
    val maxHeartRateBpm: Int = 0,
    val caloriesKcal: Double = 0.0,
    val isSensorActive: Boolean = false,
    val healthServicesConnected: Boolean = false
)

class WearExerciseTracker(context: Context) : SensorEventListener {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val exerciseClient = runCatching {
        HealthServices.getClient(appContext).exerciseClient
    }.getOrNull()

    private val sensorManager =
        appContext.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val heartRateSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    private val _biometrics = MutableStateFlow(LiveBiometricsState())
    val biometrics: StateFlow<LiveBiometricsState> = _biometrics.asStateFlow()

    private val hrSamples = mutableListOf<HeartRateSamplePoint>()
    private var healthServicesCaloriesKcal = 0.0
    private var estimatedMetCaloriesKcal = 0.0
    private var isTracking = false

    private val exerciseCallback = object : ExerciseUpdateCallback {
        override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
            val latestMetrics = update.latestMetrics

            val hrPoints = latestMetrics.getData(DataType.HEART_RATE_BPM)
            val latestHr = hrPoints.lastOrNull()?.value?.roundToInt() ?: 0
            if (latestHr in 35..230) {
                recordHeartRateReading(latestHr)
            }

            val calorieData = latestMetrics.getData(DataType.CALORIES_TOTAL)
            val totalKcal = calorieData?.total ?: 0.0
            if (totalKcal > 0.0) {
                healthServicesCaloriesKcal = totalKcal
                publishBiometrics()
            }
        }

        override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) = Unit
        override fun onRegistered() = Unit
        override fun onRegistrationFailed(throwable: Throwable) = Unit
        override fun onAvailabilityChanged(
            dataType: DataType<*, *>,
            availability: Availability
        ) = Unit
    }

    fun startSession() {
        if (isTracking) return
        isTracking = true
        hrSamples.clear()
        healthServicesCaloriesKcal = 0.0
        estimatedMetCaloriesKcal = 0.0
        _biometrics.value = LiveBiometricsState(isSensorActive = true)

        // 1. Register direct BioActive Heart Rate Sensor for immediate low-latency BPM
        runCatching {
            if (heartRateSensor != null) {
                sensorManager?.registerListener(
                    this,
                    heartRateSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }

        // 2. Start Wear Health Services Exercise Session (backed by Samsung Health on Galaxy Watch8)
        scope.launch {
            runCatching {
                val client = exerciseClient ?: return@runCatching
                val capabilities = client.getCapabilitiesAsync().await()
                val exerciseType = when {
                    ExerciseType.STRENGTH_TRAINING in capabilities.supportedExerciseTypes ->
                        ExerciseType.STRENGTH_TRAINING
                    ExerciseType.CALISTHENICS in capabilities.supportedExerciseTypes ->
                        ExerciseType.CALISTHENICS
                    else -> ExerciseType.WORKOUT
                }
                val typeCaps = capabilities.getExerciseTypeCapabilities(exerciseType)
                val requestedDataTypes = buildSet<DataType<*, *>> {
                    if (DataType.HEART_RATE_BPM in typeCaps.supportedDataTypes) {
                        add(DataType.HEART_RATE_BPM)
                    }
                    if (DataType.CALORIES_TOTAL in typeCaps.supportedDataTypes) {
                        add(DataType.CALORIES_TOTAL)
                    }
                }

                client.setUpdateCallback(exerciseCallback)
                val config = ExerciseConfig.builder(exerciseType)
                    .setDataTypes(requestedDataTypes)
                    .setIsAutoPauseAndResumeEnabled(false)
                    .setIsGpsEnabled(false)
                    .build()

                client.startExerciseAsync(config).await()
                _biometrics.value = _biometrics.value.copy(healthServicesConnected = true)
            }
        }
    }

    fun pauseSession() {
        scope.launch {
            runCatching { exerciseClient?.pauseExerciseAsync()?.await() }
        }
    }

    fun resumeSession() {
        scope.launch {
            runCatching { exerciseClient?.resumeExerciseAsync()?.await() }
        }
    }

    fun stopSession(): List<HeartRateSamplePoint> {
        isTracking = false
        runCatching {
            sensorManager?.unregisterListener(this)
        }
        scope.launch {
            runCatching {
                exerciseClient?.endExerciseAsync()?.await()
                exerciseClient?.clearUpdateCallbackAsync(exerciseCallback)?.await()
            }
        }
        _biometrics.value = _biometrics.value.copy(isSensorActive = false)
        return hrSamples.toList()
    }

    /**
     * Called once per active second so calorie expenditure smoothly increments
     * using MET formula + live Heart Rate factor, merged with Health Services calories.
     */
    fun accumulateActiveSecond(metValue: Double, userWeightKg: Float) {
        if (!isTracking) return
        val hr = _biometrics.value.currentHeartRateBpm
        val hrMultiplier = if (hr > 95) {
            1.0 + ((hr - 95).coerceAtMost(85) * 0.0055)
        } else {
            1.0
        }
        // Standard ACSM MET calorie formula: (MET * 3.5 * weightKg / 200) per minute
        val kcalPerSecond = ((metValue * 3.5 * userWeightKg) / 200.0 / 60.0) * hrMultiplier
        estimatedMetCaloriesKcal += kcalPerSecond
        publishBiometrics()
    }

    private fun recordHeartRateReading(bpm: Int) {
        val now = System.currentTimeMillis()
        hrSamples.add(HeartRateSamplePoint(now, bpm))
        publishBiometrics(latestHrOverride = bpm)
    }

    private fun publishBiometrics(latestHrOverride: Int? = null) {
        val currentHr = latestHrOverride ?: _biometrics.value.currentHeartRateBpm
        val avgHr = if (hrSamples.isNotEmpty()) {
            hrSamples.map { it.bpm }.average().roundToInt()
        } else {
            currentHr
        }
        val maxHr = if (hrSamples.isNotEmpty()) {
            hrSamples.maxOf { it.bpm }
        } else {
            currentHr
        }
        val effectiveCalories = max(healthServicesCaloriesKcal, estimatedMetCaloriesKcal)
        _biometrics.value = _biometrics.value.copy(
            currentHeartRateBpm = currentHr,
            avgHeartRateBpm = avgHr,
            maxHeartRateBpm = maxHr,
            caloriesKcal = effectiveCalories
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            val rawBpm = event.values.firstOrNull()?.roundToInt() ?: 0
            if (rawBpm in 35..230) {
                recordHeartRateReading(rawBpm)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
