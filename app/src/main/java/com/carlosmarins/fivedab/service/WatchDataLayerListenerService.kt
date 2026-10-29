package com.carlosmarins.fivedab.service

import android.content.Intent
import com.carlosmarins.fivedab.MainActivity
import com.carlosmarins.fivedab.data.CompanionDataLayerPaths
import com.carlosmarins.fivedab.data.WorkoutRepository
import com.carlosmarins.fivedab.data.WorkoutRoutine
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WatchDataLayerListenerService : WearableListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val repository = WorkoutRepository(applicationContext)

        when (messageEvent.path) {
            CompanionDataLayerPaths.PATH_SYNC_ROUTINES -> {
                val rawJson = messageEvent.data.decodeToString()
                scope.launch {
                    runCatching {
                        val updatedRoutines = json.decodeFromString<List<WorkoutRoutine>>(rawJson)
                        repository.saveAllRoutines(updatedRoutines)
                    }
                }
            }

            CompanionDataLayerPaths.PATH_REQUEST_ROUTINES -> {
                val sourceNodeId = messageEvent.sourceNodeId
                scope.launch {
                    runCatching {
                        val currentRoutines = repository.routinesFlow.first()
                        val bytes = json.encodeToString(currentRoutines).encodeToByteArray()
                        Wearable.getMessageClient(applicationContext).sendMessage(
                            sourceNodeId,
                            CompanionDataLayerPaths.PATH_SYNC_ROUTINES,
                            bytes
                        ).await()
                    }
                }
            }

            CompanionDataLayerPaths.PATH_START_ROUTINE_ON_WATCH -> {
                val routineId = messageEvent.data.decodeToString()
                val launchIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra(EXTRA_START_ROUTINE_ID, routineId)
                }
                startActivity(launchIntent)
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_START_ROUTINE_ID = "extra_start_routine_id"
    }
}
