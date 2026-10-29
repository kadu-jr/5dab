package com.carlosmarins.fivedab.service

import android.content.Context
import com.carlosmarins.fivedab.data.CompanionCuePayload
import com.carlosmarins.fivedab.data.CompanionDataLayerPaths
import com.carlosmarins.fivedab.data.CompanionWorkoutTelemetry
import com.carlosmarins.fivedab.data.WorkoutRoutine
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WatchCompanionBridge(context: Context) {

    private val appContext = context.applicationContext
    private val nodeClient = Wearable.getNodeClient(appContext)
    private val messageClient = Wearable.getMessageClient(appContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Dispatches a voice/beep cue to the paired Android phone over Bluetooth.
     * If no phone node is currently reachable, invokes [onFallbackWatchLocal]
     * so the watch plays the cue out of its built-in speaker.
     */
    fun dispatchCueToPhoneOrFallback(
        payload: CompanionCuePayload,
        onFallbackWatchLocal: () -> Unit
    ) {
        scope.launch {
            val sent = runCatching {
                val nodes = nodeClient.connectedNodes.await()
                if (nodes.isEmpty()) return@runCatching false
                val bytes = json.encodeToString(payload).encodeToByteArray()
                var delivered = false
                for (node in nodes) {
                    runCatching {
                        messageClient.sendMessage(
                            node.id,
                            CompanionDataLayerPaths.PATH_CUE_EVENT,
                            bytes
                        ).await()
                        delivered = true
                    }
                }
                delivered
            }.getOrDefault(false)

            if (!sent) {
                onFallbackWatchLocal()
            }
        }
    }

    fun sendWorkoutTelemetry(telemetry: CompanionWorkoutTelemetry) {
        scope.launch {
            runCatching {
                val nodes = nodeClient.connectedNodes.await()
                if (nodes.isEmpty()) return@runCatching
                val bytes = json.encodeToString(telemetry).encodeToByteArray()
                for (node in nodes) {
                    messageClient.sendMessage(
                        node.id,
                        CompanionDataLayerPaths.PATH_WORKOUT_TELEMETRY,
                        bytes
                    ).await()
                }
            }
        }
    }

    fun pushRoutinesSnapshotToPhone(routines: List<WorkoutRoutine>) {
        scope.launch {
            runCatching {
                val nodes = nodeClient.connectedNodes.await()
                if (nodes.isEmpty()) return@runCatching
                val bytes = json.encodeToString(routines).encodeToByteArray()
                for (node in nodes) {
                    messageClient.sendMessage(
                        node.id,
                        CompanionDataLayerPaths.PATH_SYNC_ROUTINES,
                        bytes
                    ).await()
                }
            }
        }
    }
}
