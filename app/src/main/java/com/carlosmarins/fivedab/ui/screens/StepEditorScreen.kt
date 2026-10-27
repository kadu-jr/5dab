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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.carlosmarins.fivedab.data.PresetRoutines
import com.carlosmarins.fivedab.data.StepMode
import com.carlosmarins.fivedab.data.WorkoutStep
import com.carlosmarins.fivedab.ui.components.DimensionBadge
import com.carlosmarins.fivedab.ui.components.bezelScrollableList
import com.carlosmarins.fivedab.ui.theme.WatchCoral
import com.carlosmarins.fivedab.ui.theme.WatchCyan
import com.carlosmarins.fivedab.ui.theme.WatchEmerald
import com.carlosmarins.fivedab.ui.theme.WatchSurface
import com.carlosmarins.fivedab.ui.theme.WatchTextSecondary
import java.util.Locale

@Composable
fun StepEditorScreen(
    step: WorkoutStep,
    canDelete: Boolean,
    onSave: (exerciseId: String, targetValue: Int, repPaceMs: Long, restSeconds: Int) -> Unit,
    onDelete: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    var selectedExerciseId by remember(step.stepId) { mutableStateOf(step.exerciseId) }
    var targetValue by remember(step.stepId) { mutableIntStateOf(step.targetValue) }
    var repPaceMs by remember(step.stepId) { mutableLongStateOf(step.repPaceMs) }
    var restSeconds by remember(step.stepId) { mutableIntStateOf(step.restAfterSeconds) }
    var showExercisePicker by remember { mutableStateOf(false) }

    val exercise = PresetRoutines.getExercise(selectedExerciseId)
    val isTimed = exercise.mode == StepMode.TIMED

    if (showExercisePicker) {
        ExerciseCatalogPickerScreen(
            currentExerciseId = selectedExerciseId,
            onSelectExercise = { newEx ->
                selectedExerciseId = newEx.id
                targetValue = newEx.defaultTarget
                repPaceMs = newEx.defaultRepPaceMs
                showExercisePicker = false
            }
        )
        return
    }

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
                    DimensionBadge(dimension = exercise.dimension)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exercise.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Change Exercise Button
            item {
                Chip(
                    onClick = { showExercisePicker = true },
                    label = {
                        Text(
                            text = "Change Exercise",
                            fontSize = 11.sp,
                            color = WatchCyan
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(backgroundColor = WatchSurface),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Target Reps or Seconds Adjuster
            item {
                AdjusterRow(
                    title = if (isTimed) "Hold Duration" else "Target Reps",
                    valueText = if (isTimed) "${targetValue}s" else "$targetValue reps",
                    onDecrement = {
                        val delta = if (isTimed) 5 else 1
                        targetValue = (targetValue - delta).coerceAtLeast(5)
                    },
                    onIncrement = {
                        val delta = if (isTimed) 5 else 1
                        targetValue = (targetValue + delta).coerceAtMost(300)
                    }
                )
            }

            // Rep Cadence Speed (only for Rep-based exercises)
            if (!isTimed) {
                item {
                    val secsPerRep = String.format(Locale.US, "%.1fs/rep", repPaceMs / 1000.0)
                    AdjusterRow(
                        title = "Auto-Pace Speed",
                        valueText = secsPerRep,
                        onDecrement = {
                            repPaceMs = (repPaceMs - 200L).coerceAtLeast(1000L)
                        },
                        onIncrement = {
                            repPaceMs = (repPaceMs + 200L).coerceAtMost(4500L)
                        }
                    )
                }
            }

            // Rest Timer Adjuster
            item {
                AdjusterRow(
                    title = "Rest After Set",
                    valueText = "${restSeconds}s",
                    onDecrement = {
                        restSeconds = (restSeconds - 5).coerceAtLeast(0)
                    },
                    onIncrement = {
                        restSeconds = (restSeconds + 5).coerceAtMost(180)
                    }
                )
            }

            // Save Changes Button
            item {
                Chip(
                    onClick = {
                        onSave(selectedExerciseId, targetValue, repPaceMs, restSeconds)
                    },
                    label = {
                        Text(
                            text = "Save Set",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(
                        backgroundColor = WatchEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (canDelete) {
                item {
                    Chip(
                        onClick = onDelete,
                        label = {
                            Text(
                                text = "Remove Set",
                                fontSize = 11.sp,
                                color = WatchCoral
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = WatchCoral,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = ChipDefaults.secondaryChipColors(backgroundColor = WatchSurface),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun AdjusterRow(
    title: String,
    valueText: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = WatchTextSecondary,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onDecrement,
                colors = ButtonDefaults.secondaryButtonColors(backgroundColor = WatchSurface),
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease $title",
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = valueText,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onIncrement,
                colors = ButtonDefaults.secondaryButtonColors(backgroundColor = WatchSurface),
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase $title",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ExerciseCatalogPickerScreen(
    currentExerciseId: String?,
    onSelectExercise: (com.carlosmarins.fivedab.data.ExerciseDefinition) -> Unit
) {
    val listState = rememberScalingLazyListState()
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
                Text(
                    text = "5D Exercise Catalog",
                    color = WatchCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(PresetRoutines.exerciseCatalog, key = { it.id }) { ex ->
                val isSelected = ex.id == currentExerciseId
                val unit = if (ex.mode == StepMode.TIMED) "sec" else "reps"
                Chip(
                    onClick = { onSelectExercise(ex) },
                    label = {
                        Column {
                            Text(
                                text = ex.name,
                                color = if (isSelected) WatchEmerald else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DimensionBadge(dimension = ex.dimension)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${ex.defaultTarget} $unit",
                                    color = WatchTextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors(backgroundColor = WatchSurface),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
