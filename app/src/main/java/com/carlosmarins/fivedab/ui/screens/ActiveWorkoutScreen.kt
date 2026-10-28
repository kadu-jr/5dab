package com.carlosmarins.fivedab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import com.carlosmarins.fivedab.ui.components.WatchIcons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.carlosmarins.fivedab.data.StepMode
import com.carlosmarins.fivedab.data.WorkoutSettings
import com.carlosmarins.fivedab.health.LiveBiometricsState
import com.carlosmarins.fivedab.ui.components.CircularWorkoutRing
import com.carlosmarins.fivedab.ui.components.DimensionBadge
import com.carlosmarins.fivedab.ui.components.bezelStepController
import com.carlosmarins.fivedab.ui.theme.WatchAmber
import com.carlosmarins.fivedab.ui.theme.WatchCoral
import com.carlosmarins.fivedab.ui.theme.WatchCyan
import com.carlosmarins.fivedab.ui.theme.WatchEmerald
import com.carlosmarins.fivedab.ui.theme.WatchSurface
import com.carlosmarins.fivedab.ui.theme.WatchTextSecondary
import com.carlosmarins.fivedab.ui.theme.accentColor
import com.carlosmarins.fivedab.viewmodel.ActiveWorkoutUiState
import com.carlosmarins.fivedab.viewmodel.WorkoutPhase
import java.util.Locale

@Composable
fun ActiveWorkoutScreen(
    state: ActiveWorkoutUiState,
    biometrics: LiveBiometricsState,
    settings: WorkoutSettings,
    onManualRepTap: () -> Unit,
    onCompleteOrSkipStep: () -> Unit,
    onTogglePause: () -> Unit,
    onFinishWorkout: () -> Unit,
    onCycleBezelMode: () -> Unit,
    onBezelScroll: (Float) -> Unit
) {
    val totalSteps = state.routine.steps.size.coerceAtLeast(1)
    val overallFraction = (state.currentStepIndex.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)

    val setFraction = when (state.phase) {
        WorkoutPhase.PREP -> {
            val totalPrep = settings.prepCountdownSeconds.coerceAtLeast(1)
            1f - (state.prepRemainingSeconds.toFloat() / totalPrep.toFloat())
        }
        WorkoutPhase.REST -> {
            val totalRest = state.restTotalSeconds.coerceAtLeast(1)
            state.restRemainingSeconds.toFloat() / totalRest.toFloat()
        }
        WorkoutPhase.ACTIVE_SET -> {
            val target = state.currentTarget.coerceAtLeast(1)
            state.currentProgress.toFloat() / target.toFloat()
        }
        else -> 0f
    }

    val ringColor = when (state.phase) {
        WorkoutPhase.PREP -> WatchAmber
        WorkoutPhase.REST -> WatchEmerald
        else -> state.currentExercise.dimension.accentColor()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .bezelStepController(onRotaryDelta = onBezelScroll)
    ) {
        // Dual concentric circular progress ring around the Galaxy Watch8 Classic display
        CircularWorkoutRing(
            setProgressFraction = setFraction,
            overallRoutineFraction = overallFraction,
            primaryColor = ringColor
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // Top Status Bar: Set X/Y + Live Heart Rate + Calories
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SET ${state.currentStepIndex + 1}/$totalSteps • ${formatMinutesSeconds(state.elapsedWorkoutSeconds)}",
                    color = WatchTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart Rate",
                        tint = WatchCoral,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (biometrics.currentHeartRateBpm > 0) {
                            "${biometrics.currentHeartRateBpm}"
                        } else {
                            "--"
                        },
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = WatchIcons.Fire,
                        contentDescription = "Calories",
                        tint = WatchAmber,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = String.format(Locale.US, "%.0f kcal", biometrics.caloriesKcal),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Center Phase Content
            when (state.phase) {
                WorkoutPhase.PREP -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "GET READY",
                            color = WatchAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${state.prepRemainingSeconds}",
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black
                        )
                        DimensionBadge(dimension = state.currentExercise.dimension)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.currentExercise.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                WorkoutPhase.REST -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "REST • ROTATE BEZEL ±5s",
                            color = WatchEmerald,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.restRemainingSeconds}s",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "NEXT: ${state.currentExercise.name}",
                            color = WatchCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val nextUnit = if (state.currentExercise.mode == StepMode.TIMED) "sec" else "reps"
                        Text(
                            text = "${state.currentStep.targetValue} $nextUnit • ${state.currentExercise.dimension.shortTitle}",
                            color = WatchTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }

                WorkoutPhase.PAUSED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "WORKOUT PAUSED",
                            color = WatchAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.currentExercise.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${state.totalRepsAccumulated} reps • ${state.completedSetsCount} sets done",
                            color = WatchTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                else -> {
                    // ACTIVE_SET
                    val isTimed = state.currentExercise.mode == StepMode.TIMED
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !isTimed) { onManualRepTap() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        DimensionBadge(dimension = state.currentExercise.dimension)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.currentExercise.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isTimed) {
                            val remainingSecs = (state.currentTarget - state.currentProgress).coerceAtLeast(0)
                            Text(
                                text = "${remainingSecs}s",
                                color = ringColor,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Hold • Bezel ±5s",
                                color = WatchTextSecondary,
                                fontSize = 9.sp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${state.currentProgress}",
                                    color = ringColor,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = " / ${state.currentTarget}",
                                    color = WatchTextSecondary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Text(
                                text = if (settings.autoPaceReps) {
                                    String.format(
                                        Locale.US,
                                        "Auto %.1fs/rep • Tap +1",
                                        state.currentStep.repPaceMs / 1000.0
                                    )
                                } else {
                                    "Manual Mode • Tap +1 Rep"
                                },
                                color = WatchTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // Bottom Controls Row
            if (state.phase == WorkoutPhase.PAUSED) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onFinishWorkout,
                        colors = ButtonDefaults.secondaryButtonColors(backgroundColor = WatchCoral),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = WatchIcons.Stop,
                            contentDescription = "End Workout",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Button(
                        onClick = onTogglePause,
                        colors = ButtonDefaults.primaryButtonColors(backgroundColor = WatchEmerald),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume Workout",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Button(
                        onClick = onCompleteOrSkipStep,
                        colors = ButtonDefaults.secondaryButtonColors(backgroundColor = WatchSurface),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = WatchIcons.SkipNext,
                            contentDescription = "Skip Set",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onTogglePause,
                        colors = ButtonDefaults.secondaryButtonColors(backgroundColor = WatchSurface),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = WatchIcons.Pause,
                            contentDescription = "Pause",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Bezel Mode Quick Pill
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(WatchSurface)
                            .clickable { onCycleBezelMode() }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (settings.bezelWorkoutAction == com.carlosmarins.fivedab.data.BezelWorkoutAction.ADJUST_TARGET_OR_REST) {
                                "Bezel: ±Target"
                            } else {
                                "Bezel: Skip"
                            },
                            color = WatchCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onCompleteOrSkipStep,
                        colors = ButtonDefaults.primaryButtonColors(
                            backgroundColor = ringColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (state.phase == WorkoutPhase.REST || state.phase == WorkoutPhase.PREP) {
                                WatchIcons.FastForward
                            } else {
                                Icons.Default.Check
                            },
                            contentDescription = "Complete Set / Skip Rest",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Bezel Rotation Feedback Toast Overlay
        state.bezelFeedbackToast?.let { toastText ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 28.dp)
                    .background(
                        color = WatchSurface.copy(alpha = 0.94f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = toastText,
                    color = WatchCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

fun formatMinutesSeconds(totalSeconds: Int): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", mins, secs)
}
