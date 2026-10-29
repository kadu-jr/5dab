package com.carlosmarins.fivedab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.carlosmarins.fivedab.data.AbDimension
import com.carlosmarins.fivedab.data.BezelWorkoutAction
import com.carlosmarins.fivedab.data.CompanionWorkoutTelemetry
import com.carlosmarins.fivedab.data.CompletedWorkoutSession
import com.carlosmarins.fivedab.data.ExerciseDefinition
import com.carlosmarins.fivedab.data.PresetRoutines
import com.carlosmarins.fivedab.data.StepMode
import com.carlosmarins.fivedab.data.WorkoutRepository
import com.carlosmarins.fivedab.data.WorkoutRoutine
import com.carlosmarins.fivedab.data.WorkoutSettings
import com.carlosmarins.fivedab.data.WorkoutStep
import com.carlosmarins.fivedab.health.HealthConnectSync
import com.carlosmarins.fivedab.health.LiveBiometricsState
import com.carlosmarins.fivedab.health.WearExerciseTracker
import com.carlosmarins.fivedab.service.CueEngine
import com.carlosmarins.fivedab.service.WorkoutForegroundService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs

enum class WorkoutPhase {
    IDLE,
    PREP,
    ACTIVE_SET,
    REST,
    PAUSED,
    COMPLETED
}

data class ActiveWorkoutUiState(
    val phase: WorkoutPhase = WorkoutPhase.IDLE,
    val phaseBeforePause: WorkoutPhase = WorkoutPhase.ACTIVE_SET,
    val routine: WorkoutRoutine = PresetRoutines.segundaRoutine,
    val currentStepIndex: Int = 0,
    val currentStep: WorkoutStep = PresetRoutines.segundaRoutine.steps.first(),
    val currentExercise: ExerciseDefinition = PresetRoutines.getExercise(
        PresetRoutines.segundaRoutine.steps.first().exerciseId
    ),
    val nextExercise: ExerciseDefinition? = null,
    val currentProgress: Int = 0,          // Current rep count OR elapsed seconds in current hold
    val currentTarget: Int = 25,           // Target reps OR total hold seconds for this step
    val restRemainingSeconds: Int = 0,
    val restTotalSeconds: Int = 20,
    val prepRemainingSeconds: Int = 5,
    val elapsedWorkoutSeconds: Int = 0,
    val totalRepsAccumulated: Int = 0,
    val totalHoldSecondsAccumulated: Int = 0,
    val completedSetsCount: Int = 0,
    val lastCompletedSession: CompletedWorkoutSession? = null,
    val bezelFeedbackToast: String? = null
)

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    val repository = WorkoutRepository(appContext)
    val cueEngine = CueEngine(appContext)
    val exerciseTracker = WearExerciseTracker(appContext)
    val healthConnectSync = HealthConnectSync(appContext)

    val routines: StateFlow<List<WorkoutRoutine>> = repository.routinesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PresetRoutines.allPresetRoutines
    )

    val settings: StateFlow<WorkoutSettings> = repository.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        WorkoutSettings()
    )

    val history: StateFlow<List<CompletedWorkoutSession>> = repository.historyFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val biometrics: StateFlow<LiveBiometricsState> = exerciseTracker.biometrics

    private val _workoutState = MutableStateFlow(ActiveWorkoutUiState())
    val workoutState: StateFlow<ActiveWorkoutUiState> = _workoutState.asStateFlow()

    private var phaseTickerJob: Job? = null
    private var clockTickerJob: Job? = null
    private var toastClearJob: Job? = null
    private var workoutStartTimeMs: Long = 0L
    private var rotaryAccumulator: Float = 0f
    private val trainedDimensions = linkedSetOf<AbDimension>()

    // ------------------------------------------------------------------------
    // Routine & Step Customization
    // ------------------------------------------------------------------------

    fun getRoutineById(routineId: String): WorkoutRoutine {
        return routines.value.find { it.id == routineId }
            ?: PresetRoutines.allPresetRoutines.find { it.id == routineId }
            ?: PresetRoutines.segundaRoutine
    }

    fun updateRoutineStep(
        routineId: String,
        stepId: String,
        newExerciseId: String,
        newTargetValue: Int,
        newRepPaceMs: Long,
        newRestSeconds: Int
    ) {
        viewModelScope.launch {
            val routine = getRoutineById(routineId)
            val updatedSteps = routine.steps.map { step ->
                if (step.stepId == stepId) {
                    step.copy(
                        exerciseId = newExerciseId,
                        targetValue = newTargetValue.coerceIn(1, 300),
                        repPaceMs = newRepPaceMs.coerceIn(1000L, 5000L),
                        restAfterSeconds = newRestSeconds.coerceIn(0, 180)
                    )
                } else {
                    step
                }
            }
            repository.saveRoutine(routine.copy(steps = updatedSteps))
            cueEngine.companionBridge.pushRoutinesSnapshotToPhone(routines.value)
        }
    }

    fun addStepToRoutine(routineId: String, exerciseId: String) {
        viewModelScope.launch {
            val routine = getRoutineById(routineId)
            val ex = PresetRoutines.getExercise(exerciseId)
            val newStep = WorkoutStep(
                stepId = UUID.randomUUID().toString().take(8),
                exerciseId = ex.id,
                targetValue = ex.defaultTarget,
                repPaceMs = ex.defaultRepPaceMs,
                restAfterSeconds = 20,
                roundLabel = "Set ${routine.steps.size + 1}"
            )
            repository.saveRoutine(routine.copy(steps = routine.steps + newStep))
            cueEngine.companionBridge.pushRoutinesSnapshotToPhone(routines.value)
        }
    }

    fun removeStepFromRoutine(routineId: String, stepId: String) {
        viewModelScope.launch {
            val routine = getRoutineById(routineId)
            if (routine.steps.size <= 1) return@launch
            val filtered = routine.steps.filterNot { it.stepId == stepId }
            repository.saveRoutine(routine.copy(steps = filtered))
            cueEngine.companionBridge.pushRoutinesSnapshotToPhone(routines.value)
        }
    }

    fun resetRoutine(routineId: String) {
        viewModelScope.launch {
            repository.resetRoutineToDefault(routineId)
            cueEngine.companionBridge.pushRoutinesSnapshotToPhone(routines.value)
        }
    }

    fun createNewCustomRoutine(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val customCount = routines.value.count { !it.isPreset } + 1
            val created = repository.createCustomRoutine("My 5D Ab #$customCount")
            cueEngine.companionBridge.pushRoutinesSnapshotToPhone(routines.value)
            onCreated(created.id)
        }
    }

    fun deleteCustomRoutine(routineId: String) {
        viewModelScope.launch {
            repository.deleteCustomRoutine(routineId)
            cueEngine.companionBridge.pushRoutinesSnapshotToPhone(routines.value)
        }
    }

    // ------------------------------------------------------------------------
    // Settings Updates
    // ------------------------------------------------------------------------

    fun toggleAutoPaceReps() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(autoPaceReps = !it.autoPaceReps) }
        }
    }

    fun toggleHaptics() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(hapticEnabled = !it.hapticEnabled) }
        }
    }

    fun toggleAudioTones() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(audioTonesEnabled = !it.audioTonesEnabled) }
        }
    }

    fun toggleVoiceCoach() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(voiceCoachEnabled = !it.voiceCoachEnabled) }
        }
    }

    fun toggleRouteAudioToPhone() {
        viewModelScope.launch {
            repository.updateSettings {
                it.copy(routeAudioToPhoneCompanion = !it.routeAudioToPhoneCompanion)
            }
        }
    }

    fun cycleBezelWorkoutAction() {
        viewModelScope.launch {
            repository.updateSettings { current ->
                val next = if (current.bezelWorkoutAction == BezelWorkoutAction.ADJUST_TARGET_OR_REST) {
                    BezelWorkoutAction.NAVIGATE_STEPS
                } else {
                    BezelWorkoutAction.ADJUST_TARGET_OR_REST
                }
                showBezelToast("Bezel: ${next.label}")
                current.copy(bezelWorkoutAction = next)
            }
        }
    }

    fun adjustUserWeight(deltaKg: Float) {
        viewModelScope.launch {
            repository.updateSettings { current ->
                current.copy(userWeightKg = (current.userWeightKg + deltaKg).coerceIn(35f, 180f))
            }
        }
    }

    // ------------------------------------------------------------------------
    // Active Workout Execution Engine
    // ------------------------------------------------------------------------

    fun startWorkout(routineId: String) {
        val routine = getRoutineById(routineId)
        if (routine.steps.isEmpty()) return

        val firstStep = routine.steps.first()
        val firstEx = PresetRoutines.getExercise(firstStep.exerciseId)
        val secondEx = routine.steps.getOrNull(1)?.let { PresetRoutines.getExercise(it.exerciseId) }
        val prepSecs = settings.value.prepCountdownSeconds.coerceAtLeast(3)

        workoutStartTimeMs = System.currentTimeMillis()
        trainedDimensions.clear()

        _workoutState.value = ActiveWorkoutUiState(
            phase = WorkoutPhase.PREP,
            routine = routine,
            currentStepIndex = 0,
            currentStep = firstStep,
            currentExercise = firstEx,
            nextExercise = secondEx,
            currentProgress = 0,
            currentTarget = firstStep.targetValue,
            prepRemainingSeconds = prepSecs,
            elapsedWorkoutSeconds = 0,
            totalRepsAccumulated = 0,
            totalHoldSecondsAccumulated = 0,
            completedSetsCount = 0
        )

        exerciseTracker.startSession()
        WorkoutForegroundService.startOrUpdate(
            appContext,
            "Prep: ${firstEx.name}"
        )

        startGlobalWorkoutClock()
        startPrepTicker()
    }

    private fun startGlobalWorkoutClock() {
        clockTickerJob?.cancel()
        clockTickerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val state = _workoutState.value
                if (state.phase == WorkoutPhase.ACTIVE_SET ||
                    state.phase == WorkoutPhase.REST ||
                    state.phase == WorkoutPhase.PREP
                ) {
                    _workoutState.value = state.copy(
                        elapsedWorkoutSeconds = state.elapsedWorkoutSeconds + 1
                    )
                    val met = if (state.phase == WorkoutPhase.ACTIVE_SET) {
                        state.currentExercise.metValue
                    } else {
                        2.0
                    }
                    exerciseTracker.accumulateActiveSecond(
                        metValue = met,
                        userWeightKg = settings.value.userWeightKg
                    )
                    broadcastTelemetry()
                }
            }
        }
    }

    private fun broadcastTelemetry() {
        val s = _workoutState.value
        val bio = biometrics.value
        val isActive = s.phase != WorkoutPhase.IDLE && s.phase != WorkoutPhase.COMPLETED
        cueEngine.companionBridge.sendWorkoutTelemetry(
            CompanionWorkoutTelemetry(
                isActive = isActive,
                phase = s.phase.name,
                routineName = s.routine.name,
                exerciseName = s.currentExercise.name,
                exerciseSubtitle = s.currentExercise.subtitle,
                stepIndex = s.currentStepIndex,
                totalSteps = s.routine.steps.size,
                currentProgress = when (s.phase) {
                    WorkoutPhase.REST -> s.restRemainingSeconds
                    WorkoutPhase.PREP -> s.prepRemainingSeconds
                    else -> s.currentProgress
                },
                currentTarget = when (s.phase) {
                    WorkoutPhase.REST -> s.restTotalSeconds
                    WorkoutPhase.PREP -> settings.value.prepCountdownSeconds
                    else -> s.currentTarget
                },
                isTimed = s.currentExercise.mode == StepMode.TIMED,
                heartRateBpm = bio.currentHeartRateBpm,
                caloriesKcal = bio.caloriesKcal,
                elapsedSeconds = s.elapsedWorkoutSeconds
            )
        )
    }

    private fun startPrepTicker() {
        phaseTickerJob?.cancel()
        phaseTickerJob = viewModelScope.launch {
            val s = settings.value
            while (_workoutState.value.phase == WorkoutPhase.PREP) {
                val remaining = _workoutState.value.prepRemainingSeconds
                cueEngine.onPrepCountdownTick(
                    secondsLeft = remaining,
                    hapticEnabled = s.hapticEnabled,
                    audioEnabled = s.audioTonesEnabled,
                    routeToPhoneCompanion = s.routeAudioToPhoneCompanion
                )
                delay(1000L)
                val current = _workoutState.value
                if (current.phase != WorkoutPhase.PREP) break
                if (current.prepRemainingSeconds <= 1) {
                    beginActiveSet(stepIndex = current.currentStepIndex)
                    break
                } else {
                    _workoutState.value = current.copy(
                        prepRemainingSeconds = current.prepRemainingSeconds - 1
                    )
                }
            }
        }
    }

    private fun beginActiveSet(stepIndex: Int) {
        val state = _workoutState.value
        val step = state.routine.steps.getOrNull(stepIndex) ?: run {
            finishWorkout()
            return
        }
        val ex = PresetRoutines.getExercise(step.exerciseId)
        val nextEx = state.routine.steps.getOrNull(stepIndex + 1)?.let {
            PresetRoutines.getExercise(it.exerciseId)
        }
        trainedDimensions.add(ex.dimension)

        _workoutState.value = state.copy(
            phase = WorkoutPhase.ACTIVE_SET,
            currentStepIndex = stepIndex,
            currentStep = step,
            currentExercise = ex,
            nextExercise = nextEx,
            currentProgress = 0,
            currentTarget = step.targetValue
        )
        broadcastTelemetry()

        WorkoutForegroundService.startOrUpdate(
            appContext,
            "${stepIndex + 1}/${state.routine.steps.size} • ${ex.name}"
        )

        val s = settings.value
        cueEngine.onAnnounceStepStart(
            exerciseName = ex.name,
            targetValue = step.targetValue,
            isTimed = ex.mode == StepMode.TIMED,
            hapticEnabled = s.hapticEnabled,
            audioEnabled = s.audioTonesEnabled,
            voiceEnabled = s.voiceCoachEnabled,
            routeToPhoneCompanion = s.routeAudioToPhoneCompanion
        )

        startActiveSetTicker()
    }

    private fun startActiveSetTicker() {
        phaseTickerJob?.cancel()
        phaseTickerJob = viewModelScope.launch {
            val initialEx = _workoutState.value.currentExercise
            if (initialEx.mode == StepMode.TIMED) {
                // Timed Hold (e.g., 60s Plank, 25s Side Plank, 20s Cobra Stretch)
                while (_workoutState.value.phase == WorkoutPhase.ACTIVE_SET) {
                    delay(1000L)
                    val current = _workoutState.value
                    if (current.phase != WorkoutPhase.ACTIVE_SET) break

                    val nextProgress = current.currentProgress + 1
                    val remaining = (current.currentTarget - nextProgress).coerceAtLeast(0)
                    val s = settings.value

                    _workoutState.value = current.copy(
                        currentProgress = nextProgress,
                        totalHoldSecondsAccumulated = current.totalHoldSecondsAccumulated + 1
                    )

                    if (remaining > 0) {
                        cueEngine.onHoldSecondTick(
                            remainingSeconds = remaining,
                            totalSeconds = current.currentTarget,
                            hapticEnabled = s.hapticEnabled,
                            audioEnabled = s.audioTonesEnabled,
                            voiceEnabled = s.voiceCoachEnabled,
                            routeToPhoneCompanion = s.routeAudioToPhoneCompanion
                        )
                    } else {
                        completeCurrentSet()
                        break
                    }
                }
            } else {
                // Rep-based exercise (e.g., 25x Knee Touch Crunch, 30x Reverse Crunch)
                while (_workoutState.value.phase == WorkoutPhase.ACTIVE_SET) {
                    val paceMs = _workoutState.value.currentStep.repPaceMs.coerceIn(1000L, 5000L)
                    delay(paceMs)
                    val current = _workoutState.value
                    if (current.phase != WorkoutPhase.ACTIVE_SET) break
                    if (!settings.value.autoPaceReps) continue

                    val nextRep = current.currentProgress + 1
                    val s = settings.value
                    _workoutState.value = current.copy(
                        currentProgress = nextRep,
                        totalRepsAccumulated = current.totalRepsAccumulated + 1
                    )
                    cueEngine.onRepCount(
                        currentRep = nextRep,
                        targetReps = current.currentTarget,
                        hapticEnabled = s.hapticEnabled,
                        audioEnabled = s.audioTonesEnabled,
                        voiceEnabled = s.voiceCoachEnabled,
                        routeToPhoneCompanion = s.routeAudioToPhoneCompanion
                    )

                    if (nextRep >= current.currentTarget) {
                        delay(450L)
                        completeCurrentSet()
                        break
                    }
                }
            }
        }
    }

    fun manualIncrementRep() {
        val current = _workoutState.value
        if (current.phase != WorkoutPhase.ACTIVE_SET) return
        if (current.currentExercise.mode != StepMode.REPS) return

        val nextRep = current.currentProgress + 1
        val s = settings.value
        _workoutState.value = current.copy(
            currentProgress = nextRep,
            totalRepsAccumulated = current.totalRepsAccumulated + 1
        )
        broadcastTelemetry()
        cueEngine.onRepCount(
            currentRep = nextRep,
            targetReps = current.currentTarget,
            hapticEnabled = s.hapticEnabled,
            audioEnabled = s.audioTonesEnabled,
            voiceEnabled = s.voiceCoachEnabled,
            routeToPhoneCompanion = s.routeAudioToPhoneCompanion
        )
        if (nextRep >= current.currentTarget) {
            completeCurrentSet()
        }
    }

    fun completeCurrentSet() {
        val state = _workoutState.value
        if (state.phase != WorkoutPhase.ACTIVE_SET && state.phase != WorkoutPhase.PREP) return

        // Credit any remaining uncounted reps if user manually clicked "Done Set"
        val creditReps = if (state.currentExercise.mode == StepMode.REPS &&
            state.currentProgress < state.currentTarget
        ) {
            state.currentTarget - state.currentProgress
        } else {
            0
        }

        val updatedSetsCount = state.completedSetsCount + 1
        val nextStepIndex = state.currentStepIndex + 1
        val nextStep = state.routine.steps.getOrNull(nextStepIndex)

        _workoutState.value = state.copy(
            totalRepsAccumulated = state.totalRepsAccumulated + creditReps,
            completedSetsCount = updatedSetsCount
        )

        if (nextStep == null) {
            finishWorkout()
            return
        }

        val restSecs = state.currentStep.restAfterSeconds
        if (restSecs <= 0) {
            beginActiveSet(nextStepIndex)
        } else {
            startRestPhase(nextStepIndex = nextStepIndex, restSeconds = restSecs)
        }
    }

    private fun startRestPhase(nextStepIndex: Int, restSeconds: Int) {
        val state = _workoutState.value
        val nextStep = state.routine.steps.getOrNull(nextStepIndex) ?: run {
            finishWorkout()
            return
        }
        val nextEx = PresetRoutines.getExercise(nextStep.exerciseId)
        val s = settings.value

        _workoutState.value = state.copy(
            phase = WorkoutPhase.REST,
            currentStepIndex = nextStepIndex,
            currentStep = nextStep,
            currentExercise = nextEx,
            nextExercise = nextEx,
            restRemainingSeconds = restSeconds,
            restTotalSeconds = restSeconds
        )
        broadcastTelemetry()

        WorkoutForegroundService.startOrUpdate(
            appContext,
            "Rest • Next: ${nextEx.name}"
        )

        cueEngine.onAnnounceRest(
            restSeconds = restSeconds,
            nextExerciseName = nextEx.name,
            hapticEnabled = s.hapticEnabled,
            audioEnabled = s.audioTonesEnabled,
            voiceEnabled = s.voiceCoachEnabled,
            routeToPhoneCompanion = s.routeAudioToPhoneCompanion
        )

        phaseTickerJob?.cancel()
        phaseTickerJob = viewModelScope.launch {
            while (_workoutState.value.phase == WorkoutPhase.REST) {
                val rem = _workoutState.value.restRemainingSeconds
                cueEngine.onPrepCountdownTick(
                    secondsLeft = rem,
                    hapticEnabled = settings.value.hapticEnabled,
                    audioEnabled = settings.value.audioTonesEnabled,
                    routeToPhoneCompanion = settings.value.routeAudioToPhoneCompanion
                )
                delay(1000L)
                val current = _workoutState.value
                if (current.phase != WorkoutPhase.REST) break
                if (current.restRemainingSeconds <= 1) {
                    beginActiveSet(current.currentStepIndex)
                    break
                } else {
                    _workoutState.value = current.copy(
                        restRemainingSeconds = current.restRemainingSeconds - 1
                    )
                }
            }
        }
    }

    fun skipRestOrAdvanceStep() {
        val state = _workoutState.value
        when (state.phase) {
            WorkoutPhase.PREP -> beginActiveSet(state.currentStepIndex)
            WorkoutPhase.REST -> beginActiveSet(state.currentStepIndex)
            WorkoutPhase.ACTIVE_SET -> completeCurrentSet()
            else -> Unit
        }
    }

    fun previousStep() {
        val state = _workoutState.value
        val targetIndex = (state.currentStepIndex - 1).coerceAtLeast(0)
        beginActiveSet(targetIndex)
    }

    fun togglePauseResume() {
        val state = _workoutState.value
        if (state.phase == WorkoutPhase.PAUSED) {
            exerciseTracker.resumeSession()
            val restored = state.phaseBeforePause
            _workoutState.value = state.copy(phase = restored)
            broadcastTelemetry()
            when (restored) {
                WorkoutPhase.PREP -> startPrepTicker()
                WorkoutPhase.ACTIVE_SET -> startActiveSetTicker()
                WorkoutPhase.REST -> startRestPhase(
                    nextStepIndex = state.currentStepIndex,
                    restSeconds = state.restRemainingSeconds.coerceAtLeast(3)
                )
                else -> Unit
            }
        } else if (state.phase == WorkoutPhase.ACTIVE_SET ||
            state.phase == WorkoutPhase.REST ||
            state.phase == WorkoutPhase.PREP
        ) {
            phaseTickerJob?.cancel()
            exerciseTracker.pauseSession()
            _workoutState.value = state.copy(
                phase = WorkoutPhase.PAUSED,
                phaseBeforePause = state.phase
            )
            broadcastTelemetry()
            WorkoutForegroundService.startOrUpdate(appContext, "Paused • ${state.routine.name}")
        }
    }

    fun finishWorkout() {
        phaseTickerJob?.cancel()
        clockTickerJob?.cancel()
        WorkoutForegroundService.stop(appContext)

        val state = _workoutState.value
        val bio = biometrics.value
        val hrSamples = exerciseTracker.stopSession()
        val endTimeMs = System.currentTimeMillis()
        val s = settings.value

        cueEngine.onWorkoutFinished(
            hapticEnabled = s.hapticEnabled,
            audioEnabled = s.audioTonesEnabled,
            voiceEnabled = s.voiceCoachEnabled,
            routeToPhoneCompanion = s.routeAudioToPhoneCompanion
        )
        cueEngine.companionBridge.sendWorkoutTelemetry(
            CompanionWorkoutTelemetry(isActive = false, phase = "COMPLETED")
        )

        viewModelScope.launch {
            val baseSession = CompletedWorkoutSession(
                sessionId = UUID.randomUUID().toString(),
                routineId = state.routine.id,
                routineName = state.routine.name,
                startTimeEpochMs = workoutStartTimeMs.takeIf { it > 0 }
                    ?: (endTimeMs - state.elapsedWorkoutSeconds * 1000L),
                endTimeEpochMs = endTimeMs,
                activeDurationSeconds = state.elapsedWorkoutSeconds.coerceAtLeast(1),
                totalRepsCompleted = state.totalRepsAccumulated,
                totalHoldSecondsCompleted = state.totalHoldSecondsAccumulated,
                completedSets = state.completedSetsCount,
                totalSets = state.routine.steps.size,
                caloriesBurnedKcal = bio.caloriesKcal,
                avgHeartRateBpm = bio.avgHeartRateBpm,
                maxHeartRateBpm = bio.maxHeartRateBpm,
                syncedToHealth = bio.healthServicesConnected,
                dimensionsTrained = trainedDimensions.toList()
            )

            val healthConnectSynced = if (s.autoSyncHealthConnect) {
                healthConnectSync.writeWorkoutSession(baseSession, hrSamples)
            } else {
                false
            }

            val finalSession = baseSession.copy(
                syncedToHealth = baseSession.syncedToHealth || healthConnectSynced
            )
            repository.recordCompletedSession(finalSession)

            _workoutState.value = state.copy(
                phase = WorkoutPhase.COMPLETED,
                lastCompletedSession = finalSession
            )
        }
    }

    fun resetToIdle() {
        phaseTickerJob?.cancel()
        clockTickerJob?.cancel()
        WorkoutForegroundService.stop(appContext)
        _workoutState.value = ActiveWorkoutUiState(phase = WorkoutPhase.IDLE)
    }

    // ------------------------------------------------------------------------
    // Galaxy Watch8 Classic Rotating Bezel Handler
    // ------------------------------------------------------------------------

    fun onWorkoutRotaryScroll(scrollPixels: Float) {
        rotaryAccumulator += scrollPixels
        val detentThreshold = 32f
        if (abs(rotaryAccumulator) < detentThreshold) return

        val direction = if (rotaryAccumulator > 0f) 1 else -1
        rotaryAccumulator = 0f

        cueEngine.onRotaryTick(settings.value.hapticEnabled)
        val state = _workoutState.value

        when (state.phase) {
            WorkoutPhase.REST -> {
                val delta = direction * 5
                val nextRest = (state.restRemainingSeconds + delta).coerceIn(1, 180)
                val nextTotal = maxOf(state.restTotalSeconds, nextRest)
                _workoutState.value = state.copy(
                    restRemainingSeconds = nextRest,
                    restTotalSeconds = nextTotal
                )
                showBezelToast("Rest: ${nextRest}s")
            }
            WorkoutPhase.PREP -> {
                val nextPrep = (state.prepRemainingSeconds + direction * 2).coerceIn(1, 30)
                _workoutState.value = state.copy(prepRemainingSeconds = nextPrep)
                showBezelToast("Prep: ${nextPrep}s")
            }
            WorkoutPhase.ACTIVE_SET -> {
                if (settings.value.bezelWorkoutAction == BezelWorkoutAction.ADJUST_TARGET_OR_REST) {
                    val stepDelta = if (state.currentExercise.mode == StepMode.TIMED) {
                        direction * 5
                    } else {
                        direction * 1
                    }
                    val minTarget = (state.currentProgress + 1).coerceAtLeast(5)
                    val newTarget = (state.currentTarget + stepDelta).coerceIn(minTarget, 300)
                    _workoutState.value = state.copy(currentTarget = newTarget)
                    val unit = if (state.currentExercise.mode == StepMode.TIMED) "s" else " reps"
                    showBezelToast("Target: $newTarget$unit")
                } else {
                    if (direction > 0) {
                        showBezelToast("Next Set ⏭")
                        skipRestOrAdvanceStep()
                    } else {
                        showBezelToast("⏮ Prev Set")
                        previousStep()
                    }
                }
            }
            else -> Unit
        }
    }

    private fun showBezelToast(message: String) {
        toastClearJob?.cancel()
        _workoutState.value = _workoutState.value.copy(bezelFeedbackToast = message)
        toastClearJob = viewModelScope.launch {
            delay(1400L)
            _workoutState.value = _workoutState.value.copy(bezelFeedbackToast = null)
        }
    }

    override fun onCleared() {
        cueEngine.release()
        super.onCleared()
    }
}
