package com.carlosmarins.fivedab.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.Energy
import com.carlosmarins.fivedab.data.CompletedWorkoutSession
import java.time.Instant
import java.time.ZoneOffset

class HealthConnectSync(private val context: Context) {

    val requiredPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class)
    )

    fun isAvailable(): Boolean {
        return runCatching {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        }.getOrDefault(false)
    }

    suspend fun hasAllPermissions(): Boolean {
        if (!isAvailable()) return false
        return runCatching {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            granted.containsAll(requiredPermissions)
        }.getOrDefault(false)
    }

    /**
     * Writes the finished 5D Ab workout session, calorie expenditure, and heart rate samples
     * to Health Connect so Samsung Health and Google Fit stay in sync.
     */
    suspend fun writeWorkoutSession(
        session: CompletedWorkoutSession,
        hrSamples: List<HeartRateSamplePoint>
    ): Boolean {
        return runCatching {
            if (!isAvailable()) return false
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            if (!granted.contains(HealthPermission.getWritePermission(ExerciseSessionRecord::class))) {
                return false
            }

            val startInstant = Instant.ofEpochMilli(session.startTimeEpochMs)
            val endInstant = Instant.ofEpochMilli(
                session.endTimeEpochMs.coerceAtLeast(session.startTimeEpochMs + 1000L)
            )
            val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(startInstant)

            val recordsToInsert = mutableListOf<androidx.health.connect.client.records.Record>()

            val notes = buildString {
                append("5D Ab Workout (${session.routineName}) • ")
                append("${session.totalRepsCompleted} reps • ")
                append("${session.totalHoldSecondsCompleted}s holds • ")
                append("Dimensions: ${session.dimensionsTrained.joinToString { it.code }}")
            }

            val exerciseRecord = ExerciseSessionRecord(
                startTime = startInstant,
                startZoneOffset = zoneOffset,
                endTime = endInstant,
                endZoneOffset = zoneOffset,
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_CALISTHENICS,
                title = "5D Ab: ${session.routineName}",
                notes = notes
            )
            recordsToInsert.add(exerciseRecord)

            if (session.caloriesBurnedKcal > 0.1 &&
                granted.contains(HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class))
            ) {
                recordsToInsert.add(
                    TotalCaloriesBurnedRecord(
                        startTime = startInstant,
                        startZoneOffset = zoneOffset,
                        endTime = endInstant,
                        endZoneOffset = zoneOffset,
                        energy = Energy.kilocalories(session.caloriesBurnedKcal)
                    )
                )
            }

            val validHrSamples = hrSamples.filter {
                it.timestampEpochMs in session.startTimeEpochMs..session.endTimeEpochMs &&
                    it.bpm in 35..230
            }
            if (validHrSamples.isNotEmpty() &&
                granted.contains(HealthPermission.getWritePermission(HeartRateRecord::class))
            ) {
                recordsToInsert.add(
                    HeartRateRecord(
                        startTime = startInstant,
                        startZoneOffset = zoneOffset,
                        endTime = endInstant,
                        endZoneOffset = zoneOffset,
                        samples = validHrSamples.map { point ->
                            HeartRateRecord.Sample(
                                time = Instant.ofEpochMilli(point.timestampEpochMs),
                                beatsPerMinute = point.bpm.toLong()
                            )
                        }
                    )
                )
            }

            client.insertRecords(recordsToInsert)
            true
        }.getOrDefault(false)
    }
}
