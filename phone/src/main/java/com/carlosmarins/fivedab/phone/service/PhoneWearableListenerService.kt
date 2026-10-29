package com.carlosmarins.fivedab.phone.service

import com.carlosmarins.fivedab.phone.data.CompanionCuePayload
import com.carlosmarins.fivedab.phone.data.CompanionDataLayerPaths
import com.carlosmarins.fivedab.phone.data.CompanionWorkoutTelemetry
import com.carlosmarins.fivedab.phone.data.PhoneRepository
import com.carlosmarins.fivedab.phone.data.WorkoutRoutine
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

object PhoneLiveStateHolder {
    private val _telemetry = MutableStateFlow(CompanionWorkoutTelemetry())
    val telemetry: StateFlow<CompanionWorkoutTelemetry> = _telemetry.asStateFlow()

    private val _lastCueSummary = MutableStateFlow("Ready • Waiting for Watch8 workout cues")
    val lastCueSummary: StateFlow<String> = _lastCueSummary.asStateFlow()

    fun updateTelemetry(newTelemetry: CompanionWorkoutTelemetry) {
        _telemetry.value = newTelemetry
    }

    fun updateLastCue(summary: String) {
        _lastCueSummary.value = summary
    }
}

class PhoneWearableListenerService : WearableListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val repository = PhoneRepository(applicationContext)

        when (messageEvent.path) {
            CompanionDataLayerPaths.PATH_CUE_EVENT -> {
                val raw = messageEvent.data.decodeToString()
                val payload = runCatching {
                    json.decodeFromString<CompanionCuePayload>(raw)
                }.getOrNull() ?: return

                scope.launch {
                    val relayEnabled = repository.relayEnabledFlow.first()
                    if (relayEnabled) {
                        PhoneAudioDuckingEngine.getInstance(applicationContext)
                            .playCueWithDucking(payload)
                    }
                    val desc = payload.speechText ?: "Cadence Beep Tone"
                    PhoneLiveStateHolder.updateLastCue("Last cue: \"$desc\"")
                }
            }

            CompanionDataLayerPaths.PATH_WORKOUT_TELEMETRY -> {
                val raw = messageEvent.data.decodeToString()
                runCatching {
                    val telemetry = json.decodeFromString<CompanionWorkoutTelemetry>(raw)
                    PhoneLiveStateHolder.updateTelemetry(telemetry)
                }
            }

            CompanionDataLayerPaths.PATH_SYNC_ROUTINES -> {
                val raw = messageEvent.data.decodeToString()
                scope.launch {
                    runCatching {
                        val routines = json.decodeFromString<List<WorkoutRoutine>>(raw)
                        repository.saveAllRoutines(routines)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
