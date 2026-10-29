package com.carlosmarins.fivedab.phone.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
enum class AbDimension(
    val code: String,
    val shortTitle: String,
    val muscleFocus: String,
    val colorHex: Long
) {
    FLEXION("1D", "Flexion", "Upper & Lower Abs", 0xFF2CE59B),
    LATERAL_FLEXION("2D", "Lateral Flexion", "Side Obliques", 0xFF4CA2FF),
    ROTATION("3D", "Rotation", "Rotational Core", 0xFFFF9F1C),
    COUNTER_EXTENSION("4D", "Anti-Extension", "Deep Core Stability", 0xFF00E5FF),
    COUNTER_LATERAL_FLEXION("5D", "Anti-Lateral", "Lateral Core Stability", 0xFFA970FF),
    RECOVERY("REC", "Stretch & Vacuum", "Transverse & Fascia", 0xFF64FFDA)
}

@Serializable
enum class StepMode {
    REPS,
    TIMED
}

@Serializable
data class ExerciseDefinition(
    val id: String,
    val name: String,
    val subtitle: String,
    val dimension: AbDimension,
    val mode: StepMode,
    val defaultTarget: Int,
    val defaultRepPaceMs: Long = 2000L,
    val metValue: Double = 4.5,
    val coachingCue: String
)

@Serializable
data class WorkoutStep(
    val stepId: String,
    val exerciseId: String,
    val targetValue: Int,
    val repPaceMs: Long = 2000L,
    val restAfterSeconds: Int = 25,
    val roundLabel: String = "Set 1"
)

@Serializable
data class WorkoutRoutine(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val isPreset: Boolean = true,
    val steps: List<WorkoutStep>
)

@Serializable
data class CompanionCuePayload(
    val speechText: String? = null,
    val toneType: Int? = null,
    val toneDurationMs: Int = 80,
    val exerciseName: String? = null,
    val stepIndex: Int = 0,
    val totalSteps: Int = 0,
    val currentProgress: Int = 0,
    val targetValue: Int = 0,
    val isTimed: Boolean = false
)

@Serializable
data class CompanionWorkoutTelemetry(
    val isActive: Boolean = false,
    val phase: String = "IDLE",
    val routineName: String = "",
    val exerciseName: String = "",
    val exerciseSubtitle: String = "",
    val stepIndex: Int = 0,
    val totalSteps: Int = 0,
    val currentProgress: Int = 0,
    val currentTarget: Int = 0,
    val isTimed: Boolean = false,
    val heartRateBpm: Int = 0,
    val caloriesKcal: Double = 0.0,
    val elapsedSeconds: Int = 0
)

object CompanionDataLayerPaths {
    const val PATH_CUE_EVENT = "/5dab/cue_event"
    const val PATH_WORKOUT_TELEMETRY = "/5dab/workout_telemetry"
    const val PATH_SYNC_ROUTINES = "/5dab/sync_routines"
    const val PATH_REQUEST_ROUTINES = "/5dab/request_routines"
    const val PATH_START_ROUTINE_ON_WATCH = "/5dab/start_routine"
}

object PhonePresets {
    const val EX_KNEE_TOUCH_CRUNCH = "knee_touch_crunch"
    const val EX_HEEL_TOUCH = "heel_touch_oblique"
    const val EX_STRAIGHT_CRUNCH = "straight_crunch_lying"
    const val EX_RUSSIAN_TWIST_5KG = "russian_twist_5kg"
    const val EX_PLANK_HOLD = "plank_hold"
    const val EX_SIDE_CRUNCH_LYING = "side_crunch_lying_each_side"
    const val EX_SEATED_KNEE_TUCK = "seated_knee_tuck_in_out"
    const val EX_RUSSIAN_TWIST_SUSPENDED = "russian_twist_suspended"
    const val EX_RUSSIAN_TWIST_5KG_SUSPENDED = "russian_twist_5kg_suspended"
    const val EX_BRIDGE_ALT_LEG_LIFT = "acute_bridge_alt_leg_lift"
    const val EX_REVERSE_CRUNCH = "reverse_crunch"
    const val EX_BICYCLE_CRUNCH = "bicycle_crunch"
    const val EX_FLUTTER_KICKS = "flutter_kicks"
    const val EX_SCISSOR_KICKS = "scissor_kicks"
    const val EX_LEG_RAISES = "lying_leg_raises"
    const val EX_V_UPS = "v_ups"
    const val EX_MOUNTAIN_CLIMBERS = "mountain_climbers"
    const val EX_DEAD_BUG = "dead_bug"
    const val EX_SIDE_PLANK_LEFT = "side_plank_left"
    const val EX_SIDE_PLANK_RIGHT = "side_plank_right"

    val exerciseCatalog: List<ExerciseDefinition> = listOf(
        ExerciseDefinition(
            id = EX_KNEE_TOUCH_CRUNCH,
            name = "Knee Touch",
            subtitle = "Upper Abs • Lying knee reach",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1900L,
            metValue = 4.5,
            coachingCue = "Exhale as your hands touch both knees"
        ),
        ExerciseDefinition(
            id = EX_HEEL_TOUCH,
            name = "Heel Touch",
            subtitle = "Obliques • Alternating heel taps",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1600L,
            metValue = 4.2,
            coachingCue = "Keep shoulder blades lifted and tap left-right heels"
        ),
        ExerciseDefinition(
            id = EX_STRAIGHT_CRUNCH,
            name = "Straight Crunch",
            subtitle = "40 Lying Straight Crunches",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 40,
            defaultRepPaceMs = 1800L,
            metValue = 4.6,
            coachingCue = "Keep lower back pressed into the floor"
        ),
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_5KG,
            name = "Russian Twist (5kg)",
            subtitle = "Rotational Core • 5kg Weight",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1900L,
            metValue = 5.2,
            coachingCue = "Rotate torso side to side holding the 5kg plate"
        ),
        ExerciseDefinition(
            id = EX_PLANK_HOLD,
            name = "Plank Hold",
            subtitle = "Isometric Core • 60s",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.TIMED,
            defaultTarget = 60,
            defaultRepPaceMs = 1000L,
            metValue = 4.3,
            coachingCue = "Lock glutes and brace your core in a straight line"
        ),
        ExerciseDefinition(
            id = EX_SIDE_CRUNCH_LYING,
            name = "Side Crunch (20/side)",
            subtitle = "20 Lying Oblique Crunches Each Side",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 40,
            defaultRepPaceMs = 1900L,
            metValue = 4.5,
            coachingCue = "20 reps on the left side, then 20 reps on the right side"
        ),
        ExerciseDefinition(
            id = EX_SEATED_KNEE_TUCK,
            name = "Seated Knee Tuck (In & Out)",
            subtitle = "Glute Balance • Extend & Pull Legs",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 25,
            defaultRepPaceMs = 2100L,
            metValue = 5.0,
            coachingCue = "Balance on glutes, extend legs out and pull knees back to chest"
        ),
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_SUSPENDED,
            name = "Suspended Russian Twist",
            subtitle = "Legs Elevated • Bodyweight",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1800L,
            metValue = 4.8,
            coachingCue = "Keep feet off the floor and twist shoulders side to side"
        ),
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_5KG_SUSPENDED,
            name = "Suspended Twist (5kg)",
            subtitle = "5kg Weight + Legs Elevated",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 2000L,
            metValue = 5.6,
            coachingCue = "Keep feet suspended and control the 5kg weight on each side"
        ),
        ExerciseDefinition(
            id = EX_BRIDGE_ALT_LEG_LIFT,
            name = "High Bridge Alt. Leg Lift",
            subtitle = "Acute Glute/Core Bridge • 1 Leg at a Time",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 24,
            defaultRepPaceMs = 2400L,
            metValue = 4.6,
            coachingCue = "Hold a high hip bridge and raise one straight leg at a time"
        ),
        ExerciseDefinition(
            id = EX_REVERSE_CRUNCH,
            name = "Reverse Crunch",
            subtitle = "Lower Abs • Hip lift off floor",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 25,
            defaultRepPaceMs = 2100L,
            metValue = 4.7,
            coachingCue = "Curl hips off the mat and lower slowly"
        ),
        ExerciseDefinition(
            id = EX_BICYCLE_CRUNCH,
            name = "Bicycle Crunch",
            subtitle = "Alternating Elbow-to-Knee",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1700L,
            metValue = 5.0,
            coachingCue = "Full leg extension with opposite elbow touch"
        ),
        ExerciseDefinition(
            id = EX_LEG_RAISES,
            name = "Lying Leg Raises",
            subtitle = "Lower Abs • Straight legs",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2400L,
            metValue = 4.8,
            coachingCue = "Lower legs slowly without arching your lower back"
        ),
        ExerciseDefinition(
            id = EX_FLUTTER_KICKS,
            name = "Flutter Kicks",
            subtitle = "Lower Abs • Fast alternating kicks",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 40,
            defaultRepPaceMs = 1200L,
            metValue = 4.9,
            coachingCue = "Keep legs low and core tight"
        ),
        ExerciseDefinition(
            id = EX_SCISSOR_KICKS,
            name = "Scissor Kicks",
            subtitle = "Lower Abs • Crisscross legs",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1500L,
            metValue = 4.7,
            coachingCue = "Cross legs alternately while hovering above the mat"
        ),
        ExerciseDefinition(
            id = EX_V_UPS,
            name = "V-Ups",
            subtitle = "Full Core • Jackknife V-sit",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2400L,
            metValue = 5.4,
            coachingCue = "Lift torso and straight legs simultaneously"
        ),
        ExerciseDefinition(
            id = EX_MOUNTAIN_CLIMBERS,
            name = "Mountain Climbers",
            subtitle = "Dynamic Plank Knee Drives",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 40,
            defaultRepPaceMs = 1300L,
            metValue = 6.0,
            coachingCue = "Drive knees toward chest keeping hips level"
        ),
        ExerciseDefinition(
            id = EX_DEAD_BUG,
            name = "Dead Bug",
            subtitle = "Deep Core • Contralateral extension",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 24,
            defaultRepPaceMs = 2300L,
            metValue = 4.0,
            coachingCue = "Extend opposite arm and leg keeping lower back flat"
        ),
        ExerciseDefinition(
            id = EX_SIDE_PLANK_LEFT,
            name = "Side Plank (Left)",
            subtitle = "Left Oblique Isometric Hold",
            dimension = AbDimension.COUNTER_LATERAL_FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 45,
            defaultRepPaceMs = 1000L,
            metValue = 4.2,
            coachingCue = "Keep left hip high and body aligned"
        ),
        ExerciseDefinition(
            id = EX_SIDE_PLANK_RIGHT,
            name = "Side Plank (Right)",
            subtitle = "Right Oblique Isometric Hold",
            dimension = AbDimension.COUNTER_LATERAL_FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 45,
            defaultRepPaceMs = 1000L,
            metValue = 4.2,
            coachingCue = "Keep right hip high and body aligned"
        )
    )

    private val exerciseById = exerciseCatalog.associateBy { it.id }

    fun getExercise(id: String): ExerciseDefinition {
        return exerciseById[id] ?: exerciseCatalog.first()
    }

    val allPresetRoutines: List<WorkoutRoutine> = listOf(
        WorkoutRoutine(
            id = "preset_segunda",
            name = "Mon • Set A",
            subtitle = "Knee & Heel Touch + 40 Crunch + 5kg Twist + Plank",
            description = "Monday: Knee Touch, Heel Touch, 40 Straight Crunches, Russian Twist 5kg, Plank 60s.",
            isPreset = true,
            steps = listOf(
                WorkoutStep("seg_1", EX_KNEE_TOUCH_CRUNCH, 30, 1900L, 20, "Set 1"),
                WorkoutStep("seg_2", EX_HEEL_TOUCH, 30, 1600L, 20, "Set 2"),
                WorkoutStep("seg_3", EX_STRAIGHT_CRUNCH, 40, 1800L, 25, "Set 3"),
                WorkoutStep("seg_4", EX_RUSSIAN_TWIST_5KG, 30, 1900L, 25, "Set 4"),
                WorkoutStep("seg_5", EX_PLANK_HOLD, 60, 1000L, 0, "Set 5")
            )
        ),
        WorkoutRoutine(
            id = "preset_terca",
            name = "Tue • Set B",
            subtitle = "Side Crunch 20/side + Knee Tuck + Susp. Twist + Plank",
            description = "Tuesday: 20 Side Crunches each side, Seated Knee Tuck (In & Out), Suspended Russian Twist, Plank 60s.",
            isPreset = true,
            steps = listOf(
                WorkoutStep("ter_1", EX_SIDE_CRUNCH_LYING, 40, 1900L, 20, "Set 1"),
                WorkoutStep("ter_2", EX_SEATED_KNEE_TUCK, 25, 2100L, 25, "Set 2"),
                WorkoutStep("ter_3", EX_RUSSIAN_TWIST_SUSPENDED, 30, 1800L, 25, "Set 3"),
                WorkoutStep("ter_4", EX_PLANK_HOLD, 60, 1000L, 0, "Set 4")
            )
        ),
        WorkoutRoutine(
            id = "preset_quarta",
            name = "Wed • Set C",
            subtitle = "40 Crunch + 5kg Susp. Twist + High Bridge Leg Lift",
            description = "Wednesday: 40 Straight Crunches, 5kg Suspended Russian Twist, High Bridge Alternating Leg Lift, Plank 60s.",
            isPreset = true,
            steps = listOf(
                WorkoutStep("qua_1", EX_STRAIGHT_CRUNCH, 40, 1800L, 25, "Set 1"),
                WorkoutStep("qua_2", EX_RUSSIAN_TWIST_5KG_SUSPENDED, 30, 2000L, 25, "Set 2"),
                WorkoutStep("qua_3", EX_BRIDGE_ALT_LEG_LIFT, 24, 2400L, 20, "Set 3"),
                WorkoutStep("qua_4", EX_PLANK_HOLD, 60, 1000L, 0, "Set 4")
            )
        ),
        WorkoutRoutine(
            id = "preset_quinta",
            name = "Thu • Set D",
            subtitle = "Customizable • Lower Abs & Obliques",
            description = "Thursday routine — customize exercises and reps anytime.",
            isPreset = true,
            steps = listOf(
                WorkoutStep("qui_1", EX_REVERSE_CRUNCH, 25, 2100L, 20, "Set 1"),
                WorkoutStep("qui_2", EX_BICYCLE_CRUNCH, 30, 1700L, 20, "Set 2"),
                WorkoutStep("qui_3", EX_SEATED_KNEE_TUCK, 25, 2100L, 25, "Set 3"),
                WorkoutStep("qui_4", EX_PLANK_HOLD, 60, 1000L, 0, "Set 4")
            )
        ),
        WorkoutRoutine(
            id = "preset_sexta",
            name = "Fri • Set E",
            subtitle = "Customizable • Full 5D Core Finisher",
            description = "Friday routine — customize exercises and reps anytime.",
            isPreset = true,
            steps = listOf(
                WorkoutStep("sex_1", EX_STRAIGHT_CRUNCH, 40, 1800L, 20, "Set 1"),
                WorkoutStep("sex_2", EX_RUSSIAN_TWIST_5KG_SUSPENDED, 30, 2000L, 25, "Set 2"),
                WorkoutStep("sex_3", EX_BRIDGE_ALT_LEG_LIFT, 24, 2400L, 20, "Set 3"),
                WorkoutStep("sex_4", EX_PLANK_HOLD, 60, 1000L, 0, "Set 4")
            )
        )
    )
}

private val Context.phoneDataStore: DataStore<Preferences> by preferencesDataStore(name = "five_d_ab_phone_store")

class PhoneRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val routinesFlow: Flow<List<WorkoutRoutine>> = context.phoneDataStore.data.map { prefs ->
        val raw = prefs[KEY_ROUTINES]
        if (raw.isNullOrBlank()) {
            PhonePresets.allPresetRoutines
        } else {
            runCatching {
                val decoded = json.decodeFromString<List<WorkoutRoutine>>(raw)
                if (decoded.isEmpty()) PhonePresets.allPresetRoutines else decoded
            }.getOrDefault(PhonePresets.allPresetRoutines)
        }
    }

    val relayEnabledFlow: Flow<Boolean> = context.phoneDataStore.data.map { prefs ->
        prefs[KEY_RELAY_ENABLED] ?: true
    }

    suspend fun setRelayEnabled(enabled: Boolean) {
        context.phoneDataStore.edit { prefs ->
            prefs[KEY_RELAY_ENABLED] = enabled
        }
    }

    suspend fun saveAllRoutines(routines: List<WorkoutRoutine>) {
        context.phoneDataStore.edit { prefs ->
            prefs[KEY_ROUTINES] = json.encodeToString(routines)
        }
    }

    suspend fun saveRoutine(updated: WorkoutRoutine): List<WorkoutRoutine> {
        var resultList: List<WorkoutRoutine> = PhonePresets.allPresetRoutines
        context.phoneDataStore.edit { prefs ->
            val current = prefs[KEY_ROUTINES]?.let {
                runCatching { json.decodeFromString<List<WorkoutRoutine>>(it) }.getOrNull()
            } ?: PhonePresets.allPresetRoutines
            val exists = current.any { it.id == updated.id }
            resultList = if (exists) {
                current.map { if (it.id == updated.id) updated else it }
            } else {
                current + updated
            }
            prefs[KEY_ROUTINES] = json.encodeToString(resultList)
        }
        return resultList
    }

    suspend fun createCustomRoutine(name: String): List<WorkoutRoutine> {
        val newRoutine = WorkoutRoutine(
            id = "custom_${UUID.randomUUID().toString().take(8)}",
            name = name,
            subtitle = "Custom 5D Ab Routine",
            description = "Created on Phone Companion for Galaxy Watch8 Classic.",
            isPreset = false,
            steps = listOf(
                WorkoutStep(UUID.randomUUID().toString().take(8), PhonePresets.EX_KNEE_TOUCH_CRUNCH, 30, 1900L, 20, "Set 1"),
                WorkoutStep(UUID.randomUUID().toString().take(8), PhonePresets.EX_STRAIGHT_CRUNCH, 40, 1800L, 20, "Set 2"),
                WorkoutStep(UUID.randomUUID().toString().take(8), PhonePresets.EX_RUSSIAN_TWIST_5KG, 30, 1900L, 20, "Set 3"),
                WorkoutStep(UUID.randomUUID().toString().take(8), PhonePresets.EX_PLANK_HOLD, 60, 1000L, 0, "Set 4")
            )
        )
        return saveRoutine(newRoutine)
    }

    companion object {
        private val KEY_ROUTINES = stringPreferencesKey("phone_routines_v1_json")
        private val KEY_RELAY_ENABLED = booleanPreferencesKey("phone_earbuds_relay_enabled")
    }
}
