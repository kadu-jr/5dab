package com.carlosmarins.fivedab

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.health.connect.client.PermissionController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.carlosmarins.fivedab.service.WatchDataLayerListenerService
import com.carlosmarins.fivedab.ui.screens.ActiveWorkoutScreen
import com.carlosmarins.fivedab.ui.screens.ExerciseCatalogPickerScreen
import com.carlosmarins.fivedab.ui.screens.HistoryScreen
import com.carlosmarins.fivedab.ui.screens.HomeScreen
import com.carlosmarins.fivedab.ui.screens.RoutineDetailScreen
import com.carlosmarins.fivedab.ui.screens.SettingsScreen
import com.carlosmarins.fivedab.ui.screens.StepEditorScreen
import com.carlosmarins.fivedab.ui.screens.WorkoutSummaryScreen
import com.carlosmarins.fivedab.ui.theme.FiveDAbWearTheme
import com.carlosmarins.fivedab.viewmodel.WorkoutPhase
import com.carlosmarins.fivedab.viewmodel.WorkoutViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WorkoutViewModel by viewModels()

    private val runtimePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    private val healthConnectPermissionsLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestWatchSensorPermissions()
        handleRemoteStartIntent(intent)

        setContent {
            FiveDAbWearTheme {
                FiveDAbWearApp(
                    viewModel = viewModel,
                    onRequestHealthPermissions = {
                        requestWatchSensorPermissions()
                        runCatching {
                            if (viewModel.healthConnectSync.isAvailable()) {
                                healthConnectPermissionsLauncher.launch(
                                    viewModel.healthConnectSync.requiredPermissions
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleRemoteStartIntent(intent)
    }

    private fun handleRemoteStartIntent(intent: Intent?) {
        val routineId = intent?.getStringExtra(WatchDataLayerListenerService.EXTRA_START_ROUTINE_ID)
        if (!routineId.isNullOrBlank()) {
            intent.removeExtra(WatchDataLayerListenerService.EXTRA_START_ROUTINE_ID)
            viewModel.startWorkout(routineId)
        }
    }

    private fun requestWatchSensorPermissions() {
        val perms = buildList {
            add(Manifest.permission.BODY_SENSORS)
            add(Manifest.permission.ACTIVITY_RECOGNITION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
        runCatching {
            runtimePermissionsLauncher.launch(perms)
        }
    }
}

@Composable
fun FiveDAbWearApp(
    viewModel: WorkoutViewModel,
    onRequestHealthPermissions: () -> Unit
) {
    val navController = rememberSwipeDismissableNavController()
    val routines by viewModel.routines.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val history by viewModel.history.collectAsState()
    val workoutState by viewModel.workoutState.collectAsState()
    val biometrics by viewModel.biometrics.collectAsState()

    // Automatically navigate to Active Workout or Summary screen when workout state changes
    LaunchedEffect(workoutState.phase) {
        if (workoutState.phase == WorkoutPhase.COMPLETED) {
            navController.navigate("workout_summary") {
                popUpTo("home") { inclusive = false }
            }
        } else if (workoutState.phase == WorkoutPhase.PREP &&
            navController.currentDestination?.route != "active_workout"
        ) {
            navController.navigate("active_workout")
        }
    }

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                routines = routines,
                history = history,
                onSelectRoutine = { routineId ->
                    navController.navigate("routine_detail/$routineId")
                },
                onQuickStartRoutine = { routineId ->
                    viewModel.startWorkout(routineId)
                    navController.navigate("active_workout")
                },
                onCreateCustomRoutine = {
                    viewModel.createNewCustomRoutine { newId ->
                        navController.navigate("routine_detail/$newId")
                    }
                },
                onOpenHistory = {
                    navController.navigate("history")
                },
                onOpenSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable(
            route = "routine_detail/{routineId}",
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: return@composable
            val routine = routines.find { it.id == routineId }
                ?: viewModel.getRoutineById(routineId)

            RoutineDetailScreen(
                routine = routine,
                onStartWorkout = {
                    viewModel.startWorkout(routine.id)
                    navController.navigate("active_workout")
                },
                onEditStep = { stepId ->
                    navController.navigate("step_editor/${routine.id}/$stepId")
                },
                onAddStep = {
                    navController.navigate("add_step/${routine.id}")
                },
                onResetOrDelete = {
                    if (routine.isPreset) {
                        viewModel.resetRoutine(routine.id)
                    } else {
                        viewModel.deleteCustomRoutine(routine.id)
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = "step_editor/{routineId}/{stepId}",
            arguments = listOf(
                navArgument("routineId") { type = NavType.StringType },
                navArgument("stepId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: return@composable
            val stepId = backStackEntry.arguments?.getString("stepId") ?: return@composable
            val routine = routines.find { it.id == routineId }
                ?: viewModel.getRoutineById(routineId)
            val step = routine.steps.find { it.stepId == stepId } ?: return@composable

            StepEditorScreen(
                step = step,
                canDelete = routine.steps.size > 1,
                onSave = { exId, target, paceMs, restSecs ->
                    viewModel.updateRoutineStep(
                        routineId = routineId,
                        stepId = stepId,
                        newExerciseId = exId,
                        newTargetValue = target,
                        newRepPaceMs = paceMs,
                        newRestSeconds = restSecs
                    )
                    navController.popBackStack()
                },
                onDelete = {
                    viewModel.removeStepFromRoutine(routineId, stepId)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "add_step/{routineId}",
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: return@composable
            ExerciseCatalogPickerScreen(
                currentExerciseId = null,
                onSelectExercise = { ex ->
                    viewModel.addStepToRoutine(routineId, ex.id)
                    navController.popBackStack()
                }
            )
        }

        composable("active_workout") {
            ActiveWorkoutScreen(
                state = workoutState,
                biometrics = biometrics,
                settings = settings,
                onManualRepTap = { viewModel.manualIncrementRep() },
                onCompleteOrSkipStep = { viewModel.skipRestOrAdvanceStep() },
                onTogglePause = { viewModel.togglePauseResume() },
                onFinishWorkout = { viewModel.finishWorkout() },
                onCycleBezelMode = { viewModel.cycleBezelWorkoutAction() },
                onBezelScroll = { delta -> viewModel.onWorkoutRotaryScroll(delta) }
            )
        }

        composable("workout_summary") {
            val completedSession = workoutState.lastCompletedSession
            if (completedSession != null) {
                WorkoutSummaryScreen(
                    session = completedSession,
                    onDone = {
                        viewModel.resetToIdle()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("history") {
            HistoryScreen(
                history = history,
                onRequestHealthPermissions = onRequestHealthPermissions
            )
        }

        composable("settings") {
            SettingsScreen(
                settings = settings,
                onToggleAutoPace = { viewModel.toggleAutoPaceReps() },
                onToggleHaptics = { viewModel.toggleHaptics() },
                onToggleAudioTones = { viewModel.toggleAudioTones() },
                onToggleVoiceCoach = { viewModel.toggleVoiceCoach() },
                onTogglePhoneAudioRelay = { viewModel.toggleRouteAudioToPhone() },
                onCycleBezelMode = { viewModel.cycleBezelWorkoutAction() },
                onAdjustWeight = { delta -> viewModel.adjustUserWeight(delta) }
            )
        }
    }
}
