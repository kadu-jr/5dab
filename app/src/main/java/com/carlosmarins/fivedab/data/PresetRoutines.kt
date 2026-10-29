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

    // Additional exercises in catalog for building Quinta & Sexta
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
        // --- Segunda-feira Core Moves ---
        ExerciseDefinition(
            id = EX_KNEE_TOUCH_CRUNCH,
            name = "Knee Touch",
            subtitle = "Deitado • Tocar nos joelhos",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 2000L,
            metValue = 4.5,
            coachingCue = "Suba o tronco deslizando as mãos até tocar os joelhos."
        ),
        ExerciseDefinition(
            id = EX_ALTERNATE_HEEL_TOUCH,
            name = "Heel Touch",
            subtitle = "Deitado • Toque alternado calcanhar",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1800L,
            metValue = 4.2,
            coachingCue = "Incline o tronco lateralmente tocando o calcanhar esquerdo e direito."
        ),
        ExerciseDefinition(
            id = EX_STRAIGHT_CRUNCH,
            name = "Flexão Reta (Deitado)",
            subtitle = "40 flexões abdominais retas",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 40,
            defaultRepPaceMs = 1800L,
            metValue = 4.6,
            coachingCue = "Deitado de costas, contraia o abdômen tirando as escápulas do chão."
        ),
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_5KG,
            name = "Russian Twist (5kg)",
            subtitle = "Rotação de tronco com 5kg",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1900L,
            metValue = 5.2,
            coachingCue = "Segure a anilha/peso de 5kg e gire o tronco de um lado para o outro."
        ),
        ExerciseDefinition(
            id = EX_PLANK_HOLD,
            name = "Plank 60s",
            subtitle = "Prancha isométrica 60s",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.TIMED,
            defaultTarget = 60,
            defaultRepPaceMs = 1000L,
            metValue = 4.5,
            coachingCue = "Apoie os antebraços, alinhe o corpo e mantenha o abdômen travado."
        ),

        // --- Terça-feira Core Moves ---
        ExerciseDefinition(
            id = EX_LEFT_SIDE_CRUNCH,
            name = "Flexão Lateral Esq.",
            subtitle = "20 flexões lado esquerdo (deitado)",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2000L,
            metValue = 4.3,
            coachingCue = "Deitado, contraia o oblíquo esquerdo subindo em direção ao quadril."
        ),
        ExerciseDefinition(
            id = EX_RIGHT_SIDE_CRUNCH,
            name = "Flexão Lateral Dir.",
            subtitle = "20 flexões lado direito (deitado)",
            dimension = AbDimension.LATERAL_FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2000L,
            metValue = 4.3,
            coachingCue = "Deitado, contraia o oblíquo direito subindo em direção ao quadril."
        ),
        ExerciseDefinition(
            id = EX_SEATED_LEG_TUCK,
            name = "Canivete Sentado (In & Out)",
            subtitle = "Só quadril no chão • Estende e puxa perna",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 25,
            defaultRepPaceMs = 2100L,
            metValue = 5.0,
            coachingCue = "Equilibrado no quadril, estenda as pernas à frente e recolha ao peito."
        ),
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_LEGS_UP,
            name = "Russian Twist (Perna Suspensa)",
            subtitle = "Sem peso • Pernas suspensas no ar",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1800L,
            metValue = 5.0,
            coachingCue = "Mantenha os pés fora do chão em V e gire o tronco de lado a lado."
        ),

        // --- Quarta-feira Core Moves ---
        ExerciseDefinition(
            id = EX_RUSSIAN_TWIST_5KG_LEGS_UP,
            name = "Russian Twist 5kg (Suspensa)",
            subtitle = "Com 5kg • Pernas suspensas no ar",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 2000L,
            metValue = 5.6,
            coachingCue = "Pés suspensos e peso de 5kg em mãos; gire controlando a estabilidade."
        ),
        ExerciseDefinition(
            id = EX_BRIDGE_ALT_LEG_LIFT,
            name = "Ponte + Eleva Perna Alternada",
            subtitle = "Ponte aguda • Levanta 1 perna de cada vez",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 24,
            defaultRepPaceMs = 2300L,
            metValue = 4.8,
            coachingCue = "Mantenha a ponte firme e eleve uma perna de cada vez sem cair o quadril."
        ),

        // --- Extras no Catálogo para montar Quinta e Sexta ---
        ExerciseDefinition(
            id = EX_REVERSE_CRUNCH,
            name = "Abdominal Infra Reverso",
            subtitle = "Deitado • Eleva quadril e joelhos",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 2200L,
            metValue = 4.8,
            coachingCue = "Deitado, puxe os joelhos tirando o quadril do chão com o abdômen inferior."
        ),
        ExerciseDefinition(
            id = EX_BICYCLE_CRUNCH,
            name = "Abdominal Bicicleta",
            subtitle = "Deitado • Cotovelo no joelho oposto",
            dimension = AbDimension.ROTATION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1800L,
            metValue = 5.0,
            coachingCue = "Alterne cotovelo direito no joelho esquerdo pedalando no ar."
        ),
        ExerciseDefinition(
            id = EX_LYING_LEG_RAISE,
            name = "Elevação de Pernas (Deitado)",
            subtitle = "Pernas retas subindo até 90°",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2500L,
            metValue = 4.9,
            coachingCue = "Suba as pernas estendidas e desça devagar sem arquear a lombar."
        ),
        ExerciseDefinition(
            id = EX_FLUTTER_KICKS,
            name = "Tesoura (Flutter Kicks)",
            subtitle = "Batida alternada de pernas retas",
            dimension = AbDimension.FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 40,
            defaultRepPaceMs = 1000L,
            metValue = 5.0,
            coachingCue = "Pernas quase rentes ao chão alternando movimentos curtos."
        ),
        ExerciseDefinition(
            id = EX_PLANK_LEG_LIFT,
            name = "Prancha + Eleva Perna",
            subtitle = "Prancha elevando 1 perna por vez",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.REPS,
            defaultTarget = 20,
            defaultRepPaceMs = 2200L,
            metValue = 5.0,
            coachingCue = "Na posição de prancha, levante uma perna de cada vez mantendo o tronco reto."
        ),
        ExerciseDefinition(
            id = EX_BEAR_PLANK,
            name = "Prancha 4 Apoios (Bear Hold)",
            subtitle = "Joelhos a 2cm do chão em isometria",
            dimension = AbDimension.COUNTER_EXTENSION,
            mode = StepMode.TIMED,
            defaultTarget = 45,
            defaultRepPaceMs = 1000L,
            metValue = 4.7,
            coachingCue = "Mãos sob os ombros e joelhos dobrados suspensos a 2cm do chão."
        ),
        ExerciseDefinition(
            id = EX_LEFT_SIDE_PLANK,
            name = "Prancha Lateral Esq.",
            subtitle = "Isometria lateral esquerda",
            dimension = AbDimension.COUNTER_LATERAL_FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 30,
            defaultRepPaceMs = 1000L,
            metValue = 4.4,
            coachingCue = "Apoie o antebraço esquerdo e segure o quadril alto alinhado."
        ),
        ExerciseDefinition(
            id = EX_RIGHT_SIDE_PLANK,
            name = "Prancha Lateral Dir.",
            subtitle = "Isometria lateral direita",
            dimension = AbDimension.COUNTER_LATERAL_FLEXION,
            mode = StepMode.TIMED,
            defaultTarget = 30,
            defaultRepPaceMs = 1000L,
            metValue = 4.4,
            coachingCue = "Apoie o antebraço direito e segure o quadril alto alinhado."
        ),
        ExerciseDefinition(
            id = EX_MOUNTAIN_CLIMBER,
            name = "Mountain Climber (Escalador)",
            subtitle = "Prancha alta puxando joelhos",
            dimension = AbDimension.FLEXION,
            mode = StepMode.REPS,
            defaultTarget = 30,
            defaultRepPaceMs = 1500L,
            metValue = 6.0,
            coachingCue = "Em prancha alta, puxe os joelhos alternadamente em direção ao peito."
        ),
        ExerciseDefinition(
            id = EX_STOMACH_VACUUM,
            name = "Stomach Vacuum",
            subtitle = "Contração profunda do transverso",
            dimension = AbDimension.RECOVERY,
            mode = StepMode.TIMED,
            defaultTarget = 25,
            defaultRepPaceMs = 1000L,
            metValue = 3.0,
            coachingCue = "Solte todo o ar e sugue o umbigo em direção à coluna."
        ),
        ExerciseDefinition(
            id = EX_COBRA_STRETCH,
            name = "Alongamento Cobra",
            subtitle = "Alongamento final de abdômen",
            dimension = AbDimension.RECOVERY,
            mode = StepMode.TIMED,
            defaultTarget = 30,
            defaultRepPaceMs = 1000L,
            metValue = 2.5,
            coachingCue = "De bruços, estenda os braços alongando toda a parede abdominal."
        )
    )

    private val exerciseMap: Map<String, ExerciseDefinition> =
        exerciseCatalog.associateBy { it.id }

    fun getExercise(id: String): ExerciseDefinition {
        return exerciseMap[id] ?: exerciseCatalog.first()
    }

    /**
     * SEGUNDA: Knee Touch / Heel Touch / 40 flexões retas (deitado) / Russian Twist com 5kg / Plank 60s
     */
    val segundaRoutine = WorkoutRoutine(
        id = "preset_segunda",
        name = "Seg • 5D Ab",
        subtitle = "Knee & Heel Touch • 40 Retas • Twist 5kg • Plank",
        description = "Treino de Segunda: Knee Touch, Heel Touch, 40 Flexões Retas deitado, Russian Twist com 5kg e Plank 60s.",
        isPreset = true,
        steps = listOf(
            WorkoutStep("seg_1", EX_KNEE_TOUCH_CRUNCH, targetValue = 30, repPaceMs = 2000L, restAfterSeconds = 20, roundLabel = "Série 1/5"),
            WorkoutStep("seg_2", EX_ALTERNATE_HEEL_TOUCH, targetValue = 30, repPaceMs = 1800L, restAfterSeconds = 20, roundLabel = "Série 2/5"),
            WorkoutStep("seg_3", EX_STRAIGHT_CRUNCH, targetValue = 40, repPaceMs = 1800L, restAfterSeconds = 25, roundLabel = "Série 3/5"),
            WorkoutStep("seg_4", EX_RUSSIAN_TWIST_5KG, targetValue = 30, repPaceMs = 1900L, restAfterSeconds = 25, roundLabel = "Série 4/5"),
            WorkoutStep("seg_5", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Série 5/5")
        )
    )

    /**
     * TERÇA: 20 flexões pra cada lado (deitado) / Canivete sentado (estende e traz perna) /
     * Russian Twist com perna suspensa (sem peso) / Plank 60s
     */
    val tercaRoutine = WorkoutRoutine(
        id = "preset_terca",
        name = "Ter • 5D Ab",
        subtitle = "20/20 Laterais • Canivete • Twist Susp. • Plank",
        description = "Treino de Terça: 20 Flexões para cada lado deitado, Canivete Sentado (estende e puxa a perna), Russian Twist com perna suspensa e Plank 60s.",
        isPreset = true,
        steps = listOf(
            WorkoutStep("ter_1", EX_LEFT_SIDE_CRUNCH, targetValue = 20, repPaceMs = 2000L, restAfterSeconds = 10, roundLabel = "Série 1/5"),
            WorkoutStep("ter_2", EX_RIGHT_SIDE_CRUNCH, targetValue = 20, repPaceMs = 2000L, restAfterSeconds = 20, roundLabel = "Série 2/5"),
            WorkoutStep("ter_3", EX_SEATED_LEG_TUCK, targetValue = 25, repPaceMs = 2100L, restAfterSeconds = 25, roundLabel = "Série 3/5"),
            WorkoutStep("ter_4", EX_RUSSIAN_TWIST_LEGS_UP, targetValue = 30, repPaceMs = 1800L, restAfterSeconds = 25, roundLabel = "Série 4/5"),
            WorkoutStep("ter_5", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Série 5/5")
        )
    )

    /**
     * QUARTA: 40 flexões deitado / Russian Twist com 5kg e perna suspensa /
     * Ponte aguda levantando uma perna de cada vez (+ Plank 60s)
     */
    val quartaRoutine = WorkoutRoutine(
        id = "preset_quarta",
        name = "Qua • 5D Ab",
        subtitle = "40 Retas • Twist 5kg Susp. • Ponte Unilateral",
        description = "Treino de Quarta: 40 Flexões deitado, Russian Twist com 5kg e perna suspensa, e Ponte aguda levantando uma perna de cada vez.",
        isPreset = true,
        steps = listOf(
            WorkoutStep("qua_1", EX_STRAIGHT_CRUNCH, targetValue = 40, repPaceMs = 1800L, restAfterSeconds = 25, roundLabel = "Série 1/3"),
            WorkoutStep("qua_2", EX_RUSSIAN_TWIST_5KG_LEGS_UP, targetValue = 30, repPaceMs = 2000L, restAfterSeconds = 25, roundLabel = "Série 2/3"),
            WorkoutStep("qua_3", EX_BRIDGE_ALT_LEG_LIFT, targetValue = 24, repPaceMs = 2300L, restAfterSeconds = 0, roundLabel = "Série 3/3")
        )
    )

    /**
     * QUINTA: Template pronto para customizar
     */
    val quintaRoutine = WorkoutRoutine(
        id = "preset_quinta",
        name = "Qui • 5D Ab",
        subtitle = "Infra • Bicicleta • Prancha Lateral • Plank",
        description = "Treino de Quinta (personalize os exercícios e repetições tocando nas séries).",
        isPreset = true,
        steps = listOf(
            WorkoutStep("qui_1", EX_REVERSE_CRUNCH, targetValue = 30, repPaceMs = 2200L, restAfterSeconds = 20, roundLabel = "Série 1/5"),
            WorkoutStep("qui_2", EX_BICYCLE_CRUNCH, targetValue = 30, repPaceMs = 1800L, restAfterSeconds = 20, roundLabel = "Série 2/5"),
            WorkoutStep("qui_3", EX_LEFT_SIDE_PLANK, targetValue = 30, repPaceMs = 1000L, restAfterSeconds = 10, roundLabel = "Série 3/5"),
            WorkoutStep("qui_4", EX_RIGHT_SIDE_PLANK, targetValue = 30, repPaceMs = 1000L, restAfterSeconds = 20, roundLabel = "Série 4/5"),
            WorkoutStep("qui_5", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Série 5/5")
        )
    )

    /**
     * SEXTA: Template pronto para customizar
     */
    val sextaRoutine = WorkoutRoutine(
        id = "preset_sexta",
        name = "Sex • 5D Ab",
        subtitle = "40 Retas • Canivete • Twist 5kg • Ponte • Plank",
        description = "Treino de Sexta (personalize os exercícios e repetições tocando nas séries).",
        isPreset = true,
        steps = listOf(
            WorkoutStep("sex_1", EX_STRAIGHT_CRUNCH, targetValue = 40, repPaceMs = 1800L, restAfterSeconds = 20, roundLabel = "Série 1/5"),
            WorkoutStep("sex_2", EX_SEATED_LEG_TUCK, targetValue = 25, repPaceMs = 2100L, restAfterSeconds = 20, roundLabel = "Série 2/5"),
            WorkoutStep("sex_3", EX_RUSSIAN_TWIST_5KG_LEGS_UP, targetValue = 30, repPaceMs = 2000L, restAfterSeconds = 20, roundLabel = "Série 3/5"),
            WorkoutStep("sex_4", EX_BRIDGE_ALT_LEG_LIFT, targetValue = 24, repPaceMs = 2300L, restAfterSeconds = 20, roundLabel = "Série 4/5"),
            WorkoutStep("sex_5", EX_PLANK_HOLD, targetValue = 60, repPaceMs = 1000L, restAfterSeconds = 0, roundLabel = "Série 5/5")
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
