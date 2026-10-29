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
import com.carlosmarins.fivedab.data.BezelWorkoutAction
import com.carlosmarins.fivedab.data.StepMode
import com.carlosmarins.fivedab.data.WorkoutSettings
import com.carlosmarins.fivedab.health.LiveBiometricsState
import com.carlosmarins.fivedab.ui.components.CircularWorkoutRing
import com.carlosmarins.fivedab.ui.components.WatchIcons
import com.carlosmarins.fivedab.ui.components.bezelStepController
import com.carlosmarins.fivedab.ui.theme.OneUiBlue
import com.carlosmarins.fivedab.ui.theme.OneUiButtonSurface
import com.carlosmarins.fivedab.ui.theme.OneUiCalorieOrange
import com.carlosmarins.fivedab.ui.theme.OneUiCardSurface
import com.carlosmarins.fivedab.ui.theme.OneUiHealthGreen
import com.carlosmarins.fivedab.ui.theme.OneUiHeartRed
import com.carlosmarins.fivedab.ui.theme.OneUiTextSecondary
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
        WorkoutPhase.PREP -> OneUiCalorieOrange
        WorkoutPhase.REST -> OneUiBlue
        WorkoutPhase.PAUSED -> OneUiCalorieOrange
        else -> OneUiHealthGreen
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .bezelStepController(onRotaryDelta = onBezelScroll)
    ) {
        // Samsung Health One UI Circular Progress Ring
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
            // Top Status Bar (Samsung Health style: Série + Tempo + BPM/kcal)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Série ${state.currentStepIndex + 1}/$totalSteps",
                        color = OneUiTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • ",
                        color = OneUiTextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = formatMinutesSeconds(state.elapsedWorkoutSeconds),
                        color = OneUiHealthGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "BPM",
                        tint = OneUiHeartRed,
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
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = WatchIcons.Fire,
                        contentDescription = "kcal",
                        tint = OneUiCalorieOrange,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = String.format(Locale.US, "%.0f kcal", biometrics.caloriesKcal),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Center Phase Content
            when (state.phase) {
                WorkoutPhase.PREP -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PREPARE-SE",
                            color = OneUiCalorieOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.prepRemainingSeconds}",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.currentExercise.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = state.currentExercise.subtitle,
                            color = OneUiTextSecondary,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                WorkoutPhase.REST -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DESCANSO",
                            color = OneUiBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatMinutesSeconds(state.restRemainingSeconds),
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "A seguir: ${state.currentExercise.name}",
                            color = OneUiHealthGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val nextUnit = if (state.currentExercise.mode == StepMode.TIMED) "s" else " reps"
                        Text(
                            text = "${state.currentStep.targetValue}$nextUnit • Gire a coroa ±5s",
                            color = OneUiTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }

                WorkoutPhase.PAUSED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PAUSADO",
                            color = OneUiCalorieOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.currentExercise.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${state.totalRepsAccumulated} reps • ${state.completedSetsCount} séries feitas",
                            color = OneUiTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                else -> {
                    // ACTIVE_SET (Samsung Health clean workout display)
                    val isTimed = state.currentExercise.mode == StepMode.TIMED
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !isTimed) { onManualRepTap() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = state.currentExercise.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = state.currentExercise.subtitle,
                            color = OneUiTextSecondary,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isTimed) {
                            val remainingSecs = (state.currentTarget - state.currentProgress).coerceAtLeast(0)
                            Text(
                                text = formatMinutesSeconds(remainingSecs),
                                color = OneUiHealthGreen,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Isometria • Coroa ±5s",
                                color = OneUiTextSecondary,
                                fontSize = 9.sp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${state.currentProgress}",
                                    color = OneUiHealthGreen,
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " / ${state.currentTarget}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Text(
                                text = if (settings.autoPaceReps) {
                                    String.format(
                                        Locale.US,
                                        "Ritmo %.1fs • Toque +1",
                                        state.currentStep.repPaceMs / 1000.0
                                    )
                                } else {
                                    "Manual • Toque +1 rep"
                                },
                                color = OneUiTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // Bottom One UI Watch Controls Row
            if (state.phase == WorkoutPhase.PAUSED) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onFinishWorkout,
                        colors = ButtonDefaults.secondaryButtonColors(backgroundColor = OneUiHeartRed),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = WatchIcons.Stop,
                            contentDescription = "Encerrar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Button(
                        onClick = onTogglePause,
                        colors = ButtonDefaults.primaryButtonColors(backgroundColor = OneUiHealthGreen),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Retomar",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Button(
                        onClick = onCompleteOrSkipStep,
                        colors = ButtonDefaults.secondaryButtonColors(backgroundColor = OneUiButtonSurface),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = WatchIcons.SkipNext,
                            contentDescription = "Pular Série",
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
                        colors = ButtonDefaults.secondaryButtonColors(backgroundColor = OneUiButtonSurface),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = WatchIcons.Pause,
                            contentDescription = "Pausar",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Bezel Mode Quick Pill (One UI Pill)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(OneUiCardSurface)
                            .clickable { onCycleBezelMode() }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (settings.bezelWorkoutAction == BezelWorkoutAction.ADJUST_TARGET_OR_REST) {
                                "Coroa: ±Meta"
                            } else {
                                "Coroa: Pular"
                            },
                            color = OneUiBlue,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold
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
                            contentDescription = "Concluir Série / Pular",
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
                        color = OneUiCardSurface.copy(alpha = 0.96f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = toastText,
                    color = OneUiHealthGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatMinutesSeconds(totalSeconds: Int): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}
