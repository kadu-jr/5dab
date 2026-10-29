package com.carlosmarins.fivedab.data

import kotlinx.serialization.Serializable

@Serializable
enum class AbDimension(
    val code: String,
    val shortTitle: String,
    val muscleFocus: String,
    val colorHex: Long
) {
    FLEXION(
        code = "1D",
        shortTitle = "Flexion",
        muscleFocus = "Upper & Lower Abs",
        colorHex = 0xFF2CE59B // Samsung Health Green
    ),
    LATERAL_FLEXION(
        code = "2D",
        shortTitle = "Lateral Flexion",
        muscleFocus = "Side Obliques",
        colorHex = 0xFF4CA2FF // One UI Blue
    ),
    ROTATION(
        code = "3D",
        shortTitle = "Rotation",
        muscleFocus = "Rotational Core",
        colorHex = 0xFFFF9F1C // One UI Amber
    ),
    COUNTER_EXTENSION(
        code = "4D",
        shortTitle = "Anti-Extension",
        muscleFocus = "Deep Core Stability",
        colorHex = 0xFF00E5FF // One UI Cyan
    ),
    COUNTER_LATERAL_FLEXION(
        code = "5D",
        shortTitle = "Anti-Lateral",
        muscleFocus = "Lateral Core Stability",
        colorHex = 0xFFA970FF // One UI Violet
    ),
    RECOVERY(
        code = "REC",
        shortTitle = "Stretch & Vacuum",
        muscleFocus = "Transverse & Fascia",
        colorHex = 0xFF64FFDA
    )
}

@Serializable
enum class StepMode {
    REPS,
    TIMED
}

@Serializable
enum class BezelWorkoutAction(val label: String) {
    ADJUST_TARGET_OR_REST("Adjust Reps / Timer"),
    NAVIGATE_STEPS("Prev / Next Set")
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
data class WorkoutSettings(
    val autoPaceReps: Boolean = true,
    val hapticEnabled: Boolean = true,
    val audioTonesEnabled: Boolean = true,
    val voiceCoachEnabled: Boolean = true,
    val routeAudioToPhoneCompanion: Boolean = true,
    val bezelWorkoutAction: BezelWorkoutAction = BezelWorkoutAction.ADJUST_TARGET_OR_REST,
    val prepCountdownSeconds: Int = 5,
    val userWeightKg: Float = 75f,
    val autoSyncHealthConnect: Boolean = true
)

@Serializable
data class CompletedWorkoutSession(
    val sessionId: String,
    val routineId: String,
    val routineName: String,
    val startTimeEpochMs: Long,
    val endTimeEpochMs: Long,
    val activeDurationSeconds: Int,
    val totalRepsCompleted: Int,
    val totalHoldSecondsCompleted: Int,
    val completedSets: Int,
    val totalSets: Int,
    val caloriesBurnedKcal: Double,
    val avgHeartRateBpm: Int,
    val maxHeartRateBpm: Int,
    val syncedToHealth: Boolean,
    val dimensionsTrained: List<AbDimension> = emptyList()
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

