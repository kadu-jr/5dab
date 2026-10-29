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
        shortTitle = "Flexão Reto",
        muscleFocus = "Abdômen Superior e Inferior",
        colorHex = 0xFF2CE59B // Samsung Health Green
    ),
    LATERAL_FLEXION(
        code = "2D",
        shortTitle = "Flexão Lateral",
        muscleFocus = "Oblíquos Laterais",
        colorHex = 0xFF4CA2FF // One UI Blue
    ),
    ROTATION(
        code = "3D",
        shortTitle = "Rotação",
        muscleFocus = "Oblíquos e Core Rotacional",
        colorHex = 0xFFFF9F1C // One UI Amber
    ),
    COUNTER_EXTENSION(
        code = "4D",
        shortTitle = "Estabilidade",
        muscleFocus = "Core Profundo e Lombar",
        colorHex = 0xFF00E5FF // One UI Cyan
    ),
    COUNTER_LATERAL_FLEXION(
        code = "5D",
        shortTitle = "Anti-Lateral",
        muscleFocus = "Estabilidade Oblíqua",
        colorHex = 0xFFA970FF // One UI Violet
    ),
    RECOVERY(
        code = "REC",
        shortTitle = "Alongamento",
        muscleFocus = "Mobilidade e Fáscia",
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
    ADJUST_TARGET_OR_REST("Ajustar Reps / Tempo"),
    NAVIGATE_STEPS("Avançar / Voltar Série")
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
    val roundLabel: String = "Série 1"
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
