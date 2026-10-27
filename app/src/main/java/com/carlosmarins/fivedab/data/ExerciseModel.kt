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
        colorHex = 0xFF00E5FF
    ),
    LATERAL_FLEXION(
        code = "2D",
        shortTitle = "Lateral Flexion",
        muscleFocus = "Side Obliques",
        colorHex = 0xFF00E676
    ),
    ROTATION(
        code = "3D",
        shortTitle = "Rotation",
        muscleFocus = "Rotational Core",
        colorHex = 0xFFFFB300
    ),
    COUNTER_EXTENSION(
        code = "4D",
        shortTitle = "Counter-Extension",
        muscleFocus = "Deep Anti-Arch Core",
        colorHex = 0xFFFF5252
    ),
    COUNTER_LATERAL_FLEXION(
        code = "5D",
        shortTitle = "Counter-Lateral",
        muscleFocus = "Anti-Tilt Lateral Core",
        colorHex = 0xFFE040FB
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
    val defaultRepPaceMs: Long = 2200L,
    val metValue: Double = 4.5,
    val coachingCue: String
)

@Serializable
data class WorkoutStep(
    val stepId: String,
    val exerciseId: String,
    val targetValue: Int,
    val repPaceMs: Long = 2200L,
    val restAfterSeconds: Int = 20,
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
