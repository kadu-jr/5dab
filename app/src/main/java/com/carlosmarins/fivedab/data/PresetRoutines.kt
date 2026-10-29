package com.carlosmarins.fivedab.data

object PresetRoutines {

    const val EX_KNEE_TOUCH_CRUNCH = "knee_touch_crunch"
    const val EX_ALTERNATE_HEEL_TOUCH = "alternate_heel_touch"
    const val EX_STRAIGHT_CRUNCH = "straight_crunch_lying"
    const val EX_RUSSIAN_TWIST_5KG = "russian_twist_5kg"
    const val EX_PLANK_HOLD = "plank_hold"

    const val EX_LEFT_SIDE_CRUNCH = "left_side_crunch"
    const val EX_RIGHT_SIDE_CRUNCH = "right_side_crunch"
    const val EX_SEATED_LEG_TUCK = "seated_leg_tuck"
    const val EX_RUSSIAN_TWIST_LEGS_UP = "russian_twist_legs_up"

    const val EX_RUSSIAN_TWIST_5KG_LEGS_UP = "russian_twist_5kg_legs_up"
    const val EX_BRIDGE_ALT_LEG_LIFT = "bridge_alt_leg_lift"

    // Additional exercises in catalog for building Thursday & Friday
    const val EX_REVERSE_CRUNCH = "reverse_crunch"
    const val EX_BICYCLE_CRUNCH = "bicycle_crunch"
    const val EX_LYING_LEG_RAISE = "lying_leg_raise"
    const val EX_FLUTTER_KICKS = "flutter_kicks"
    const val EX_PLANK_LEG_LIFT = "plank_leg_lift"
    const val EX_BEAR_PLANK = "bear_plank"
    const val EX_LEFT_SIDE_PLANK = "left_side_plank"
    const val EX_RIGHT_SIDE_PLANK = "right_side_plank"
    const val EX_MOUNTAIN_CLIMBER = "mountain_climber"
    const val EX_STOMACH_VACUUM = "stomach_vacuum"
    const val EX_COBRA_STRETCH = "cobra_stretch"

    val exerciseCatalog: List<ExerciseDefinition> = listOf(
        // --- Monday Core Moves ---
        ExerciseDefinition(
            id = EX_KNEE_TOUCH_CRUNCH,
            name = "Knee Touch",
            subtitle = "Lying • Reach & touch knees",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 2000L,
            metValue = 4.5,
            coachingCue = "Curl upper torso and slide hands up to touch your knees."
        ),
        ExerciseDefinition(
            id = EX_ALTERNATE_HEEL_TOUCH,
            name = "Heel Touch",
            subtitle = "Lying • Alternating heel touches",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1800L,
            metValue = 4.2,
            coachingCue = "Crunch side-to-side tapping left and right heels."
        ),
        ExerciseDefinition(
            id = EX_STRAIGHT_CRUNCH,
            name = "Straight Crunch (Lying)",
            subtitle = "40 straight lying crunches",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 40,
            defaultRepPaceMs = 1800L,
            metValue = 4.6,
            coachingCue = "Lie flat on your back and curl shoulder blades off the mat."
        ),
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_5KG,
            name = "Russian Twist (5kg)",
            subtitle = "Weighted 5kg torso rotation",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1900L,
            metValue = 5.2,
            coachingCue = "Hold a 5kg plate/dumbbell and rotate ribcage side to side."
        ),
        ExerciseDefinition(
            id = EX_PLANK_HOLD,
            name = "Plank 60s",
            subtitle = "Forearm isometric plank",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.TIMED,
            defaultTarget = 60,
            defaultRepPaceMs = 1000L,
            metValue = 4.5,
            coachingCue = "Elbows under shoulders, brace core and glutes in a straight line."
        ),

        // --- Tuesday Core Moves ---
        ExerciseDefinition(
            id = EX_LEFT_SIDE_CRUNCH,
            name = "Left Side Crunch (Lying)",
            subtitle = "20 lying left oblique crunches",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2000L,
            metValue = 4.3,
            coachingCue = "Lie on your side and crunch left oblique upward for 20 reps."
        ),
        ExerciseDefinition(
            id = EX_RIGHT_SIDE_CRUNCH,
            name = "Right Side Crunch (Lying)",
            subtitle = "20 lying right oblique crunches",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2000L,
            metValue = 4.3,
            coachingCue = "Lie on your side and crunch right oblique upward for 20 reps."
        ),
        ExerciseDefinition(
            id = EX_SEATED_LEG_TUCK,
            name = "Seated Knee Tuck (In & Out)",
            subtitle = "Balance on glutes • Extend & pull legs",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 25,
            defaultRepPaceMs = 2100L,
            metValue = 5.0,
            coachingCue = "Balance on tailbone, extend legs out straight and pull knees back to chest."
        ),
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_LEGS_UP,
            name = "Russian Twist (Legs Up)",
            subtitle = "Bodyweight • Feet suspended in air",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1800L,
            metValue = 5.0,
            coachingCue = "Keep feet suspended off the floor in a V-sit and rotate torso."
        ),

        // --- Wednesday Core Moves ---
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_5KG_LEGS_UP,
            name = "Russian Twist 5kg (Legs Up)",
            subtitle = "5kg weight • Feet suspended in air",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 2000L,
            metValue = 5.6,
            coachingCue = "Keep feet suspended while rotating a 5kg weight side to side."
        ),
        ExerciseDefinition(
            id = EX_BRIDGE_ALT_LEG_LIFT,
            name = "High Bridge Alt. Leg Lift",
            subtitle = "Acute bridge • Lift one leg at a time",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 24,
            defaultRepPaceMs = 2300L,
            metValue = 4.8,
            coachingCue = "Hold a high bridge and alternate lifting one leg at a time without dropping hips."
        ),

        // --- Extra Catalog Moves for Customizing Thursday & Friday ---
        ExerciseDefinition(
            id = EX_REVERSE_CRUNCH,
            name = "Reverse Crunch",
            subtitle = "Lying • Curl hips & knees up",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 2200L,
            metValue = 4.8,
            coachingCue = "Curl pelvis off the mat toward ribs using your lower abs."
        ),
        ExerciseDefinition(
            id = EX_BICYCLE_CRUNCH,
            name = "Bicycle Crunch",
            subtitle = "Lying • Alternating elbow to knee",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1800L,
            metValue = 5.0,
            coachingCue = "Alternate touching opposite elbow to knee while pedaling legs."
        ),
        ExerciseDefinition(
            id = EX_LYING_LEG_RAISE,
            name = "Lying Leg Raise",
            subtitle = "Straight legs up to 90°",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2500L,
            metValue = 4.9,
            coachingCue = "Raise straight legs to vertical and lower slowly without arching lower back."
        ),
        ExerciseDefinition(
            id = EX_FLUTTER_KICKS,
            name = "Flutter Kicks",
            subtitle = "Alternating straight-leg kicks",
            dimension = AbDimension.FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 40,
            defaultRepPaceMs = 1000L,
            metValue = 5.0,
            coachingCue = "Hover legs just above the floor and alternate short rapid kicks."
        ),
        ExerciseDefinition(
            id = EX_PLANK_LEG_LIFT,
            name = "Plank Alt. Leg Lift",
            subtitle = "Forearm plank lifting 1 leg at a time",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2200L,
            metValue = 5.0,
            coachingCue = "In forearm plank, alternate lifting each leg without twisting hips."
        ),
        ExerciseDefinition(
            id = EX_BEAR_PLANK,
            name = "4-Point Bear Plank",
            subtitle = "Knees hovering 2cm off floor",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.TIMED,
            defaultTarget = 45,
            defaultRepPaceMs = 1000L,
            metValue = 4.7,
            coachingCue = "Hands under shoulders, knees bent 90° hovering just off the mat."
        ),
        ExerciseDefinition(
            id = EX_LEFT_SIDE_PLANK,
            name = "Left Side Plank",
            subtitle = "Left oblique isometric hold",
            dimension = AbDimension.COUNTER_LATERAL_FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 30,
            defaultRepPaceMs = 1000L,
            metValue = 4.4,
            coachingCue = "Support on left forearm and hold hips high in a straight line."
        ),
        ExerciseDefinition(
            id = EX_RIGHT_SIDE_PLANK,
            name = "Right Side Plank",
            subtitle = "Right oblique isometric hold",
            dimension = AbDimension.COUNTER_LATERAL_FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 30,
            defaultRepPaceMs = 1000L,
            metValue = 4.4,
            coachingCue = "Support on right forearm and hold hips high in a straight line."
        ),
        ExerciseDefinition(
            id = EX_MOUNTAIN_CLIMBER,
            name = "Mountain Climber",
            subtitle = "High plank alternating knee drives",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1500L,
            metValue = 6.0,
            coachingCue = "In high plank, drive knees alternately toward chest."
        ),
        ExerciseDefinition(
            id = EX_STOMACH_VACUUM,
            name = "Stomach Vacuum",
            subtitle = "Deep transverse core hold",
            dimension = AbDimension.RECOVERY,
            mode = StepMode.TIMED,
            defaultTarget = 25,
            defaultRepPaceMs = 1000L,
            metValue = 3.0,
            coachingCue = "Exhale fully and draw navel deep toward spine."
        ),
        ExerciseDefinition(
            id = EX_COBRA_STRETCH,
            name = "Cobra Ab Stretch",
            subtitle = "Abdominal fascia cooldown stretch",
            dimension = AbDimension.RECOVERY,
            mode = StepMode.TIMED,
            defaultTarget = 30,
            defaultRepPaceMs = 1000L,
            metValue = 2.5,
            coachingCue = "Lie prone and press chest gently upward to stretch abs."
        )
    )

    private val exerciseMap: Map<String, ExerciseDefinition> =
        exerciseCatalog.associateBy { it.id }

    fun getExercise(id: String): ExerciseDefinition {
        return exerciseMap[id] ?: exerciseCatalog.first()
    }

    /**
     * MONDAY: Knee Touch / Heel Touch / 40 Straight Crunches / Russian Twist 5kg / Plank 60s
     */
    val segundaRoutine = WorkoutRoutine(
        id = "preset_segunda",
        name = "Mon • 5D Ab",
        subtitle = "Knee & Heel Touch • 40 Crunch • Twist 5kg • Plank",
        description = "Monday: Knee Touch, Heel Touch, 40 Straight Lying Crunches, Russian Twist 5kg, and Plank 60s.",
        isPreset = true,
        steps = listOf(
            WorkoutStep("seg_1", EX_KNEE_TOUCH_CRUNCH, targetValue = 30, repPaceMs = 2000L, restAfterSeconds = 20, roundLabel = "Set 1/5"),
            WorkoutStep("seg_2", EX_ALTERNATE_HEEL_TOUCH, targetValue = 30, repPaceMs = 1800L, restAfterSeconds = 20, roundLabel = "Set 2/5"),
            WorkoutStep("seg_3", EX_STRAIGHT_CRUNCH, targetValue = 40, repPaceMs = 1800L, restAfterSeconds = 25, roundLabel = "Set 3/5"),
            WorkoutStep("seg_4", EX_RUSSIAN_TWIST_5KG, targetValue = 30, repPaceMs = 1900L, restAfterSeconds = 25, roundLabel = "Set 4/5"),
            WorkoutStep("seg_5", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Set 5/5")
        )
    )

    /**
     * TUESDAY: 20 Side Crunches each side / Seated Knee Tuck (In & Out) /
     * Russian Twist (Legs Up, no weight) / Plank 60s
     */
    val tercaRoutine = WorkoutRoutine(
        id = "preset_terca",
        name = "Tue • 5D Ab",
        subtitle = "20/20 Side Crunch • Seated Tuck • Twist • Plank",
        description = "Tuesday: 20 Lying Side Crunches each side, Seated Knee Tuck (In & Out), Russian Twist Legs Up, and Plank 60s.",
        isPreset = true,
        steps = listOf(
            WorkoutStep("ter_1", EX_LEFT_SIDE_CRUNCH, targetValue = 20, repPaceMs = 2000L, restAfterSeconds = 10, roundLabel = "Set 1/5"),
            WorkoutStep("ter_2", EX_RIGHT_SIDE_CRUNCH, targetValue = 20, repPaceMs = 2000L, restAfterSeconds = 20, roundLabel = "Set 2/5"),
            WorkoutStep("ter_3", EX_SEATED_LEG_TUCK, targetValue = 25, repPaceMs = 2100L, restAfterSeconds = 25, roundLabel = "Set 3/5"),
            WorkoutStep("ter_4", EX_RUSSIAN_TWIST_LEGS_UP, targetValue = 30, repPaceMs = 1800L, restAfterSeconds = 25, roundLabel = "Set 4/5"),
            WorkoutStep("ter_5", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Set 5/5")
        )
    )

    /**
     * WEDNESDAY: 40 Straight Crunches / Russian Twist 5kg (Legs Up) /
     * High Bridge Alternating Leg Lift
     */
    val quartaRoutine = WorkoutRoutine(
        id = "preset_quarta",
        name = "Wed • 5D Ab",
        subtitle = "40 Crunch • Twist 5kg Legs Up • Bridge Leg Lift",
        description = "Wednesday: 40 Straight Lying Crunches, Russian Twist 5kg with Legs Up, and High Bridge Alternating Leg Lift.",
        isPreset = true,
        steps = listOf(
            WorkoutStep("qua_1", EX_STRAIGHT_CRUNCH, targetValue = 40, repPaceMs = 1800L, restAfterSeconds = 25, roundLabel = "Set 1/3"),
            WorkoutStep("qua_2", EX_RUSSIAN_TWIST_5KG_LEGS_UP, targetValue = 30, repPaceMs = 2000L, restAfterSeconds = 25, roundLabel = "Set 2/3"),
            WorkoutStep("qua_3", EX_BRIDGE_ALT_LEG_LIFT, targetValue = 24, repPaceMs = 2300L, restAfterSeconds = 0, roundLabel = "Set 3/3")
        )
    )

    /**
     * THURSDAY: Ready to customize
     */
    val quintaRoutine = WorkoutRoutine(
        id = "preset_quinta",
        name = "Thu • 5D Ab",
        subtitle = "Reverse • Bicycle • Side Plank • Plank 60s",
        description = "Thursday template (tap any set to customize exercises and reps).",
        isPreset = true,
        steps = listOf(
            WorkoutStep("qui_1", EX_REVERSE_CRUNCH, targetValue = 30, repPaceMs = 2200L, restAfterSeconds = 20, roundLabel = "Set 1/5"),
            WorkoutStep("qui_2", EX_BICYCLE_CRUNCH, targetValue = 30, repPaceMs = 1800L, restAfterSeconds = 20, roundLabel = "Set 2/5"),
            WorkoutStep("qui_3", EX_LEFT_SIDE_PLANK, targetValue = 30, repPaceMs = 1000L, restAfterSeconds = 10, roundLabel = "Set 3/5"),
            WorkoutStep("qui_4", EX_RIGHT_SIDE_PLANK, targetValue = 30, repPaceMs = 1000L, restAfterSeconds = 20, roundLabel = "Set 4/5"),
            WorkoutStep("qui_5", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Set 5/5")
        )
    )

    /**
     * FRIDAY: Ready to customize
     */
    val sextaRoutine = WorkoutRoutine(
        id = "preset_sexta",
        name = "Fri • 5D Ab",
        subtitle = "40 Crunch • Seated Tuck • Twist 5kg • Bridge • Plank",
        description = "Friday template (tap any set to customize exercises and reps).",
        isPreset = true,
        steps = listOf(
            WorkoutStep("sex_1", EX_STRAIGHT_CRUNCH, targetValue = 40, repPaceMs = 1800L, restAfterSeconds = 20, roundLabel = "Set 1/5"),
            WorkoutStep("sex_2", EX_SEATED_LEG_TUCK, targetValue = 25, repPaceMs = 2100L, restAfterSeconds = 20, roundLabel = "Set 2/5"),
            WorkoutStep("sex_3", EX_RUSSIAN_TWIST_5KG_LEGS_UP, targetValue = 30, repPaceMs = 2000L, restAfterSeconds = 20, roundLabel = "Set 3/5"),
            WorkoutStep("sex_4", EX_BRIDGE_ALT_LEG_LIFT, targetValue = 24, repPaceMs = 2300L, restAfterSeconds = 20, roundLabel = "Set 4/5"),
            WorkoutStep("sex_5", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Set 5/5")
        )
    )

    val allPresetRoutines: List<WorkoutRoutine> = listOf(
        segundaRoutine,
        tercaRoutine,
        quartaRoutine,
        quintaRoutine,
        sextaRoutine
    )
}
