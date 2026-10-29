package com.carlosmarins.fivedab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.carlosmarins.fivedab.data.PresetRoutines
import com.carlosmarins.fivedab.data.StepMode
import com.carlosmarins.fivedab.data.WorkoutRoutine
import com.carlosmarins.fivedab.ui.components.bezelScrollableList
import com.carlosmarins.fivedab.ui.theme.OneUiBlue
import com.carlosmarins.fivedab.ui.theme.OneUiCardSurface
import com.carlosmarins.fivedab.ui.theme.OneUiHealthGreen
import com.carlosmarins.fivedab.ui.theme.OneUiHeartRed
import com.carlosmarins.fivedab.ui.theme.OneUiTextSecondary

@Composable
fun RoutineDetailScreen(
    routine: WorkoutRoutine,
    onStartWorkout: () -> Unit,
    onEditStep: (String) -> Unit,
    onAddStep: () -> Unit,
    onResetOrDelete: () -> Unit
) {
    val listState = rememberScalingLazyListState()

    val estimatedSeconds = routine.steps.sumOf { step ->
        val ex = PresetRoutines.getExercise(step.exerciseId)
        val activeSecs = if (ex.mode == StepMode.TIMED) {
            step.targetValue
        } else {
            ((step.targetValue * step.repPaceMs) / 1000L).toInt()
        }
        activeSecs + step.restAfterSeconds
    }
    val estMinutes = (estimatedSeconds / 60).coerceAtLeast(1)

    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top = 26.dp,
                bottom = 36.dp,
                start = 10.dp,
                end = 10.dp
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .bezelScrollableList(listState)
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = routine.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${routine.steps.size} exercícios • ~${estMinutes} min",
                        color = OneUiHealthGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Botão Iniciar estilo Samsung Health
            item {
                Chip(
                    onClick = onStartWorkout,
                    label = {
                        Text(
                            text = "INICIAR",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(
                        backgroundColor = OneUiHealthGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = "Toque no exercício para editar",
                    color = OneUiTextSecondary,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Lista de séries
            itemsIndexed(routine.steps, key = { _, step -> step.stepId }) { index, step ->
                val ex = PresetRoutines.getExercise(step.exerciseId)
                val unit = if (ex.mode == StepMode.TIMED) "s" else " reps"

                Chip(
                    onClick = { onEditStep(step.stepId) },
                    label = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${index + 1}. ${ex.name}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = OneUiTextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = "${step.targetValue}$unit • Descanso ${step.restAfterSeconds}s",
                                color = OneUiHealthGreen,
                                fontSize = 10.sp
                            )
                            Text(
                                text = ex.subtitle,
                                color = OneUiTextSecondary,
                                fontSize = 8.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors(
                        backgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Adicionar Exercício
            item {
                Chip(
                    onClick = onAddStep,
                    label = {
                        Text(
                            text = "Adicionar Exercício",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = OneUiBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(
                        backgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Restaurar Padrão ou Excluir
            item {
                Chip(
                    onClick = onResetOrDelete,
                    label = {
                        Text(
                            text = if (routine.isPreset) "Restaurar Padrão" else "Excluir Treino",
                            fontSize = 11.sp,
                            color = if (routine.isPreset) OneUiTextSecondary else OneUiHeartRed
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = if (routine.isPreset) Icons.Default.Refresh else Icons.Default.Delete,
                            contentDescription = null,
                            tint = if (routine.isPreset) OneUiTextSecondary else OneUiHeartRed,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(
                        backgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
