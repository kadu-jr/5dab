package com.carlosmarins.fivedab.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "five_d_ab_store")

class WorkoutRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val routinesFlow: Flow<List<WorkoutRoutine>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_ROUTINES]
        if (raw.isNullOrBlank()) {
            PresetRoutines.allPresetRoutines
        } else {
            runCatching {
                val saved = json.decodeFromString<List<WorkoutRoutine>>(raw)
                if (saved.isEmpty()) PresetRoutines.allPresetRoutines else saved
            }.getOrDefault(PresetRoutines.allPresetRoutines)
        }
    }

    val settingsFlow: Flow<WorkoutSettings> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_SETTINGS]
        if (raw.isNullOrBlank()) {
            WorkoutSettings()
        } else {
            runCatching {
                json.decodeFromString<WorkoutSettings>(raw)
            }.getOrDefault(WorkoutSettings())
        }
    }

    val historyFlow: Flow<List<CompletedWorkoutSession>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_HISTORY]
        if (raw.isNullOrBlank()) {
            emptyList()
        } else {
            runCatching {
                json.decodeFromString<List<CompletedWorkoutSession>>(raw)
            }.getOrDefault(emptyList())
        }
    }

    suspend fun saveRoutine(updatedRoutine: WorkoutRoutine) {
        context.dataStore.edit { prefs ->
            val currentList = prefs[KEY_ROUTINES]?.let {
                runCatching { json.decodeFromString<List<WorkoutRoutine>>(it) }.getOrNull()
            } ?: PresetRoutines.allPresetRoutines

            val exists = currentList.any { it.id == updatedRoutine.id }
            val nextList = if (exists) {
                currentList.map { if (it.id == updatedRoutine.id) updatedRoutine else it }
            } else {
                currentList + updatedRoutine
            }
            prefs[KEY_ROUTINES] = json.encodeToString(nextList)
        }
    }

    suspend fun saveAllRoutines(routines: List<WorkoutRoutine>) {
        if (routines.isEmpty()) return
        context.dataStore.edit { prefs ->
            prefs[KEY_ROUTINES] = json.encodeToString(routines)
        }
    }

    suspend fun createCustomRoutine(name: String): WorkoutRoutine {
        val newRoutine = WorkoutRoutine(
            id = "custom_${UUID.randomUUID().toString().take(8)}",
            name = name,
            subtitle = "Custom 5D Ab Routine",
            description = "Custom 5D Ab workout on Galaxy Watch8 Classic.",
            isPreset = false,
            steps = listOf(
                WorkoutStep(
                    stepId = UUID.randomUUID().toString().take(8),
                    exerciseId = PresetRoutines.EX_KNEE_TOUCH_CRUNCH,
                    targetValue = 30,
                    repPaceMs = 2000L,
                    restAfterSeconds = 20,
                    roundLabel = "Set 1"
                ),
                WorkoutStep(
                    stepId = UUID.randomUUID().toString().take(8),
                    exerciseId = PresetRoutines.EX_STRAIGHT_CRUNCH,
                    targetValue = 40,
                    repPaceMs = 1800L,
                    restAfterSeconds = 20,
                    roundLabel = "Set 2"
                ),
                WorkoutStep(
                    stepId = UUID.randomUUID().toString().take(8),
                    exerciseId = PresetRoutines.EX_RUSSIAN_TWIST_5KG,
                    targetValue = 30,
                    repPaceMs = 1900L,
                    restAfterSeconds = 20,
                    roundLabel = "Set 3"
                ),
                WorkoutStep(
                    stepId = UUID.randomUUID().toString().take(8),
                    exerciseId = PresetRoutines.EX_PLANK_HOLD,
                    targetValue = 60,
                    repPaceMs = 1000L,
                    restAfterSeconds = 0,
                    roundLabel = "Set 4"
                )
            )
        )
        saveRoutine(newRoutine)
        return newRoutine
    }

    suspend fun resetRoutineToDefault(routineId: String) {
        val defaultPreset = PresetRoutines.allPresetRoutines.find { it.id == routineId } ?: return
        saveRoutine(defaultPreset)
    }

    suspend fun deleteCustomRoutine(routineId: String) {
        context.dataStore.edit { prefs ->
            val currentList = prefs[KEY_ROUTINES]?.let {
                runCatching { json.decodeFromString<List<WorkoutRoutine>>(it) }.getOrNull()
            } ?: PresetRoutines.allPresetRoutines
            val nextList = currentList.filterNot { it.id == routineId && !it.isPreset }
            prefs[KEY_ROUTINES] = json.encodeToString(nextList)
        }
    }

    suspend fun updateSettings(transform: (WorkoutSettings) -> WorkoutSettings) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_SETTINGS]?.let {
                runCatching { json.decodeFromString<WorkoutSettings>(it) }.getOrNull()
            } ?: WorkoutSettings()
            val updated = transform(current)
            prefs[KEY_SETTINGS] = json.encodeToString(updated)
        }
    }

    suspend fun recordCompletedSession(session: CompletedWorkoutSession) {
        context.dataStore.edit { prefs ->
            val currentHistory = prefs[KEY_HISTORY]?.let {
                runCatching { json.decodeFromString<List<CompletedWorkoutSession>>(it) }.getOrNull()
            } ?: emptyList()
            val updated = (listOf(session) + currentHistory).take(100)
            prefs[KEY_HISTORY] = json.encodeToString(updated)
        }
    }

    companion object {
        private val KEY_ROUTINES = stringPreferencesKey("saved_routines_v3_json")
        private val KEY_SETTINGS = stringPreferencesKey("workout_settings_json")
        private val KEY_HISTORY = stringPreferencesKey("workout_history_json")

        fun computeStreakDays(history: List<CompletedWorkoutSession>): Int {
            if (history.isEmpty()) return 0
            val zone = ZoneId.systemDefault()
            val uniqueDates = history
                .map { Instant.ofEpochMilli(it.endTimeEpochMs).atZone(zone).toLocalDate() }
                .distinct()
                .sortedDescending()

            val today = LocalDate.now(zone)
            val mostRecent = uniqueDates.firstOrNull() ?: return 0
            if (mostRecent != today && mostRecent != today.minusDays(1)) {
                return 0
            }

            var streak = 1
            var expectedDate = mostRecent.minusDays(1)
            for (i in 1 until uniqueDates.size) {
                if (uniqueDates[i] == expectedDate) {
                    streak++
                    expectedDate = expectedDate.minusDays(1)
                } else {
                    break
                }
            }
            return streak
        }
    }
}
