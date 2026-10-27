package com.carlosmarins.fivedab.data

object PresetRoutines {

    const val EX_KNEE_TOUCH_CRUNCH = "knee_touch_crunch"
    const val EX_HALF_CRUNCH = "half_crunch"
    const val EX_REVERSE_CRUNCH = "reverse_crunch"
    const val EX_LYING_KNEE_TUCK = "lying_knee_tuck"
    const val EX_ALTERNATE_HEEL_TOUCH = "alternate_heel_touch"
    const val EX_LEFT_SIDE_CRUNCH = "left_side_crunch"
    const val EX_RIGHT_SIDE_CRUNCH = "right_side_crunch"
    const val EX_RUSSIAN_TWIST = "russian_twist"
    const val EX_PLANK_HOLD = "plank_hold"
    const val EX_BEAR_PLANK = "bear_plank"
    const val EX_PLANK_LEG_LIFT = "plank_leg_lift"
    const val EX_LEFT_SIDE_PLANK = "left_side_plank"
    const val EX_RIGHT_SIDE_PLANK = "right_side_plank"
    const val EX_STOMACH_VACUUM = "stomach_vacuum"
    const val EX_COBRA_STRETCH = "cobra_stretch"

    val exerciseCatalog: List<ExerciseDefinition> = listOf(
        ExerciseDefinition(
            id = EX_KNEE_TOUCH_CRUNCH,
            name = "Knee Touch Crunch",
            subtitle = "Upper Ab Flexion • 卷腹摸膝",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 25,
            defaultRepPaceMs = 2200L,
            metValue = 4.5,
            coachingCue = "Exhale as fingertips slide up thighs to touch knees; keep lower back pressed to mat."
        ),
        ExerciseDefinition(
            id = EX_HALF_CRUNCH,
            name = "Half Crunch",
            subtitle = "Upper Ab Flexion • 半程卷腹",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2000L,
            metValue = 4.3,
            coachingCue = "Curl shoulder blades off the floor with chin tucked; pause briefly at peak contraction."
        ),
        ExerciseDefinition(
            id = EX_REVERSE_CRUNCH,
            name = "Bent-Knee Reverse Crunch",
            subtitle = "Lower Ab Flexion • 反向屈腿卷腹",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 2400L,
            metValue = 4.8,
            coachingCue = "Curl pelvis upward toward ribs using lower abs; lower hips slowly with control."
        ),
        ExerciseDefinition(
            id = EX_LYING_KNEE_TUCK,
            name = "Lying Knee Tuck",
            subtitle = "Lower Ab Flexion • 屈膝收腹",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2300L,
            metValue = 5.0,
            coachingCue = "Draw knees in toward chest while bracing core, then extend legs without arching back."
        ),
        ExerciseDefinition(
            id = EX_ALTERNATE_HEEL_TOUCH,
            name = "Alternate Heel Touch",
            subtitle = "Oblique Lateral Flexion • 仰卧交替摸脚",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 15,
            defaultRepPaceMs = 2000L,
            metValue = 4.2,
            coachingCue = "Lift shoulders slightly and crunch side-to-side to tap left and right heels."
        ),
        ExerciseDefinition(
            id = EX_LEFT_SIDE_CRUNCH,
            name = "Left Side-Lying Crunch",
            subtitle = "Left Oblique Flexion • 侧卧左侧卷腹",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 15,
            defaultRepPaceMs = 2100L,
            metValue = 4.3,
            coachingCue = "Lie on right side and contract left oblique to lift torso toward left hip."
        ),
        ExerciseDefinition(
            id = EX_RIGHT_SIDE_CRUNCH,
            name = "Right Side-Lying Crunch",
            subtitle = "Right Oblique Flexion • 侧卧右侧卷腹",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 15,
            defaultRepPaceMs = 2100L,
            metValue = 4.3,
            coachingCue = "Lie on left side and contract right oblique to lift torso toward right hip."
        ),
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST,
            name = "Modified Russian Twist",
            subtitle = "Core Rotation • 简易俄罗斯转体",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 1900L,
            metValue = 4.8,
            coachingCue = "Lean back 45° with heels planted; rotate ribcage left and right with braced abs."
        ),
        ExerciseDefinition(
            id = EX_PLANK_HOLD,
            name = "Plank Hold",
            subtitle = "Counter-Extension • 平板支撑",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.TIMED,
            defaultTarget = 60,
            defaultRepPaceMs = 1000L,
            metValue = 4.5,
            coachingCue = "Elbows under shoulders, glutes tight, posterior pelvic tilt—do not let lower back sag."
        ),
        ExerciseDefinition(
            id = EX_BEAR_PLANK,
            name = "4-Point Bear Plank",
            subtitle = "Counter-Extension • 四点支撑",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.TIMED,
            defaultTarget = 30,
            defaultRepPaceMs = 1000L,
            metValue = 4.7,
            coachingCue = "Hands under shoulders, knees bent 90° hovering 2 cm off the floor; breathe steadily."
        ),
        ExerciseDefinition(
            id = EX_PLANK_LEG_LIFT,
            name = "Plank Alt. Leg Lift",
            subtitle = "Counter-Extension • 平板支撑交替抬腿",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 12,
            defaultRepPaceMs = 2500L,
            metValue = 5.0,
            coachingCue = "In forearm plank, alternate lifting each leg without letting hips twist or drop."
        ),
        ExerciseDefinition(
            id = EX_LEFT_SIDE_PLANK,
            name = "Left Side Plank Hold",
            subtitle = "Counter-Lateral • 左侧屈膝侧支撑",
            dimension = AbDimension.COUNTER_LATERAL_FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 25,
            defaultRepPaceMs = 1000L,
            metValue = 4.4,
            coachingCue = "Support on left forearm, keep hips elevated in a straight line resisting side drop."
        ),
        ExerciseDefinition(
            id = EX_RIGHT_SIDE_PLANK,
            name = "Right Side Plank Hold",
            subtitle = "Counter-Lateral • 右侧屈膝侧支撑",
            dimension = AbDimension.COUNTER_LATERAL_FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 25,
            defaultRepPaceMs = 1000L,
            metValue = 4.4,
            coachingCue = "Support on right forearm, keep hips elevated in a straight line resisting side drop."
        ),
        ExerciseDefinition(
            id = EX_STOMACH_VACUUM,
            name = "Stomach Vacuum Hold",
            subtitle = "Transverse Core • 真空腹训练",
            dimension = AbDimension.RECOVERY,
            mode = StepMode.TIMED,
            defaultTarget = 20,
            defaultRepPaceMs = 1000L,
            metValue = 3.0,
            coachingCue = "Exhale completely and draw navel deep toward spine while taking shallow rib breaths."
        ),
        ExerciseDefinition(
            id = EX_COBRA_STRETCH,
            name = "Cobra Ab Stretch",
            subtitle = "Fascia Release • 腹部拉伸",
            dimension = AbDimension.RECOVERY,
            mode = StepMode.TIMED,
            defaultTarget = 25,
            defaultRepPaceMs = 1000L,
            metValue = 2.5,
            coachingCue = "Lie prone, press chest gently upward while keeping hips grounded to stretch abs."
        )
    )

    private val exerciseMap: Map<String, ExerciseDefinition> =
        exerciseCatalog.associateBy { it.id }

    fun getExercise(id: String): ExerciseDefinition {
        return exerciseMap[id] ?: exerciseCatalog.first()
    }

    /**
     * Preset 1: Classic Samsung Health / Keep "5D Ab" 10-Set Routine (五维腹肌初级 01)
     */
    val classic5DAbRoutine = WorkoutRoutine(
        id = "preset_classic_5d_ab",
        name = "5D Ab Classic",
        subtitle = "10 Sets • Original Samsung Health Plan",
        description = "The exact 10-set 5D Ab workout removed from Samsung Health: 2 sets each of Knee Touch Crunch, Reverse Crunch, Alternate Heel Touch, Russian Twist, and 60s Plank.",
        isPreset = true,
        steps = listOf(
            WorkoutStep("c_1", EX_KNEE_TOUCH_CRUNCH, targetValue = 25, repPaceMs = 2200L, restAfterSeconds = 20, roundLabel = "Set 1/2"),
            WorkoutStep("c_2", EX_KNEE_TOUCH_CRUNCH, targetValue = 25, repPaceMs = 2200L, restAfterSeconds = 25, roundLabel = "Set 2/2"),
            WorkoutStep("c_3", EX_REVERSE_CRUNCH, targetValue = 30, repPaceMs = 2400L, restAfterSeconds = 20, roundLabel = "Set 1/2"),
            WorkoutStep("c_4", EX_REVERSE_CRUNCH, targetValue = 30, repPaceMs = 2400L, restAfterSeconds = 25, roundLabel = "Set 2/2"),
            WorkoutStep("c_5", EX_ALTERNATE_HEEL_TOUCH, targetValue = 15, repPaceMs = 2000L, restAfterSeconds = 20, roundLabel = "Set 1/2"),
            WorkoutStep("c_6", EX_ALTERNATE_HEEL_TOUCH, targetValue = 15, repPaceMs = 2000L, restAfterSeconds = 25, roundLabel = "Set 2/2"),
            WorkoutStep("c_7", EX_RUSSIAN_TWIST, targetValue = 20, repPaceMs = 1900L, restAfterSeconds = 20, roundLabel = "Set 1/2"),
            WorkoutStep("c_8", EX_RUSSIAN_TWIST, targetValue = 20, repPaceMs = 1900L, restAfterSeconds = 25, roundLabel = "Set 2/2"),
            WorkoutStep("c_9", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 30, roundLabel = "Set 1/2"),
            WorkoutStep("c_10", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Set 2/2")
        )
    )

    /**
     * Preset 2: Full 5-Dimension Sculpt & Anti-Lateral Circuit (3 Rounds)
     * Explicitly trains all 5 biomechanical functions including Counter-Lateral Flexion (Side Planks),
     * Stomach Vacuum, and Cobra Stretch.
     */
    val full5DSculptRoutine = WorkoutRoutine(
        id = "preset_full_5d_sculpt",
        name = "5D Complete Sculpt",
        subtitle = "3 Rounds • All 5 Biomechanical Axes",
        description = "3-round circuit covering Flexion, Lateral Flexion, Rotation, Counter-Extension, and Counter-Lateral Side Planks plus Stomach Vacuum and Cobra Stretch.",
        isPreset = true,
        steps = listOf(
            // Round 1
            WorkoutStep("s_1", EX_KNEE_TOUCH_CRUNCH, targetValue = 15, repPaceMs = 2200L, restAfterSeconds = 15, roundLabel = "Round 1"),
            WorkoutStep("s_2", EX_REVERSE_CRUNCH, targetValue = 15, repPaceMs = 2400L, restAfterSeconds = 15, roundLabel = "Round 1"),
            WorkoutStep("s_3", EX_RUSSIAN_TWIST, targetValue = 20, repPaceMs = 1900L, restAfterSeconds = 15, roundLabel = "Round 1"),
            WorkoutStep("s_4", EX_LEFT_SIDE_PLANK, targetValue = 25, repPaceMs = 1000L, restAfterSeconds = 10, roundLabel = "Round 1"),
            WorkoutStep("s_5", EX_RIGHT_SIDE_PLANK, targetValue = 25, repPaceMs = 1000L, restAfterSeconds = 15, roundLabel = "Round 1"),
            WorkoutStep("s_6", EX_COBRA_STRETCH, targetValue = 20, repPaceMs = 1000L, restAfterSeconds = 25, roundLabel = "Round 1"),
            // Round 2
            WorkoutStep("s_7", EX_KNEE_TOUCH_CRUNCH, targetValue = 20, repPaceMs = 2200L, restAfterSeconds = 15, roundLabel = "Round 2"),
            WorkoutStep("s_8", EX_REVERSE_CRUNCH, targetValue = 20, repPaceMs = 2400L, restAfterSeconds = 15, roundLabel = "Round 2"),
            WorkoutStep("s_9", EX_ALTERNATE_HEEL_TOUCH, targetValue = 20, repPaceMs = 2000L, restAfterSeconds = 15, roundLabel = "Round 2"),
            WorkoutStep("s_10", EX_PLANK_HOLD, targetValue = 40, repPaceMs = 1000L, restAfterSeconds = 15, roundLabel = "Round 2"),
            WorkoutStep("s_11", EX_COBRA_STRETCH, targetValue = 20, repPaceMs = 1000L, restAfterSeconds = 25, roundLabel = "Round 2"),
            // Round 3
            WorkoutStep("s_12", EX_LYING_KNEE_TUCK, targetValue = 20, repPaceMs = 2300L, restAfterSeconds = 15, roundLabel = "Round 3"),
            WorkoutStep("s_13", EX_RUSSIAN_TWIST, targetValue = 20, repPaceMs = 1900L, restAfterSeconds = 15, roundLabel = "Round 3"),
            WorkoutStep("s_14", EX_PLANK_LEG_LIFT, targetValue = 12, repPaceMs = 2500L, restAfterSeconds = 15, roundLabel = "Round 3"),
            WorkoutStep("s_15", EX_STOMACH_VACUUM, targetValue = 20, repPaceMs = 1000L, restAfterSeconds = 10, roundLabel = "Round 3"),
            WorkoutStep("s_16", EX_COBRA_STRETCH, targetValue = 25, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Cooldown")
        )
    )

    /**
     * Preset 3: 5D Ab 6-Move 3-Round Circuit (Half Crunch, Knee Tuck, L/R Side Crunch, Twist, Bear Plank)
     */
    val circuit6MoveRoutine = WorkoutRoutine(
        id = "preset_6move_circuit",
        name = "5D 6-Move Circuit",
        subtitle = "18 Sets • 3 Rounds × 6 Exercises",
        description = "High-endurance 3-round circuit combining Half Crunch, Lying Knee Tuck, Left & Right Side Crunches, Russian Twist, and 4-Point Bear Plank.",
        isPreset = true,
        steps = (1..3).flatMap { round ->
            listOf(
                WorkoutStep("m_${round}_1", EX_HALF_CRUNCH, targetValue = 20, repPaceMs = 2000L, restAfterSeconds = 15, roundLabel = "Round $round/3"),
                WorkoutStep("m_${round}_2", EX_LYING_KNEE_TUCK, targetValue = 20, repPaceMs = 2300L, restAfterSeconds = 15, roundLabel = "Round $round/3"),
                WorkoutStep("m_${round}_3", EX_LEFT_SIDE_CRUNCH, targetValue = 15, repPaceMs = 2100L, restAfterSeconds = 10, roundLabel = "Round $round/3"),
                WorkoutStep("m_${round}_4", EX_RIGHT_SIDE_CRUNCH, targetValue = 15, repPaceMs = 2100L, restAfterSeconds = 15, roundLabel = "Round $round/3"),
                WorkoutStep("m_${round}_5", EX_RUSSIAN_TWIST, targetValue = 20, repPaceMs = 1900L, restAfterSeconds = 15, roundLabel = "Round $round/3"),
                WorkoutStep(
                    "m_${round}_6",
                    EX_BEAR_PLANK,
                    targetValue = 30,
                    repPaceMs = 1000L,
                    restAfterSeconds = if (round < 3) 45 else 0,
                    roundLabel = "Round $round/3"
                )
            )
        }
    )

    val allPresetRoutines: List<WorkoutRoutine> = listOf(
        classic5DAbRoutine,
        full5DSculptRoutine,
        circuit6MoveRoutine
    )
}
