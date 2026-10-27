package com.carlosmarins.fivedab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.carlosmarins.fivedab.data.CompletedWorkoutSession
import com.carlosmarins.fivedab.data.PresetRoutines
import com.carlosmarins.fivedab.data.WorkoutRepository
import com.carlosmarins.fivedab.data.WorkoutRoutine
import com.carlosmarins.fivedab.ui.components.FiveDPentagonRadar
import com.carlosmarins.fivedab.ui.components.bezelScrollableList
import com.carlosmarins.fivedab.ui.theme.WatchAmber
import com.carlosmarins.fivedab.ui.theme.WatchCyan
import com.carlosmarins.fivedab.ui.theme.WatchEmerald
import com.carlosmarins.fivedab.ui.theme.WatchSurface
import com.carlosmarins.fivedab.ui.theme.WatchSurfaceVariant
import com.carlosmarins.fivedab.ui.theme.WatchTextSecondary

@Composable
fun HomeScreen(
    routines: List<WorkoutRoutine>,
    history: List<CompletedWorkoutSession>,
    onSelectRoutine: (String) -> Unit,
    onQuickStartRoutine: (String) -> Unit,
    onCreateCustomRoutine: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    val streakDays = WorkoutRepository.computeStreakDays(history)
    val totalSessions = history.size

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top = 28.dp,
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
            // Header with 5D Pentagon & Streak
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FiveDPentagonRadar(
                            activeDimensions = com.carlosmarins.fivedab.data.AbDimension.entries,
                            size = 28.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "5D AB WORKOUT",
                            color = WatchCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (totalSessions > 0) {
                            "🔥 $streakDays-day streak • $totalSessions done"
                        } else {
                            "Galaxy Watch8 Classic Edition"
                        },
                        color = if (streakDays > 0) WatchAmber else WatchTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Routine Cards
            items(routines, key = { it.id }) { routine ->
                val dimensions = routine.steps
                    .map { PresetRoutines.getExercise(it.exerciseId).dimension }
                    .distinct()

                Card(
                    onClick = { onSelectRoutine(routine.id) },
                    backgroundPainter = ChipDefaults.chipBackgroundPainter(
                        startBackgroundColor = WatchSurface,
                        endBackgroundColor = WatchSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = routine.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${routine.steps.size} sets • ${dimensions.size}D axes",
                                color = WatchCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = routine.subtitle,
                                color = WatchTextSecondary,
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { onQuickStartRoutine(routine.id) },
                            colors = ButtonDefaults.primaryButtonColors(
                                backgroundColor = WatchEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start ${routine.name}",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Create Custom 5D Routine
            item {
                Chip(
                    onClick = onCreateCustomRoutine,
                    label = {
                        Text(
                            text = "New Custom Routine",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    icon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(22.dp)
                                .background(WatchCyan.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = WatchCyan,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors(
                        backgroundColor = WatchSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // History & Samsung Health Sync
            item {
                Chip(
                    onClick = onOpenHistory,
                    label = {
                        Text(
                            text = "History & Health Sync",
                            fontSize = 12.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = WatchEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(
                        backgroundColor = WatchSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Settings
            item {
                Chip(
                    onClick = onOpenSettings,
                    label = {
                        Text(
                            text = "Bezel, Voice & Pacing",
                            fontSize = 12.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = WatchTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(
                        backgroundColor = WatchSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
