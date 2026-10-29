package com.carlosmarins.fivedab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.carlosmarins.fivedab.data.WorkoutRepository
import com.carlosmarins.fivedab.data.WorkoutRoutine
import com.carlosmarins.fivedab.ui.components.WatchIcons
import com.carlosmarins.fivedab.ui.components.bezelScrollableList
import com.carlosmarins.fivedab.ui.theme.OneUiBlue
import com.carlosmarins.fivedab.ui.theme.OneUiCardSurface
import com.carlosmarins.fivedab.ui.theme.OneUiHealthGreen
import com.carlosmarins.fivedab.ui.theme.OneUiTextSecondary

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
                bottom = 38.dp,
                start = 10.dp,
                end = 10.dp
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .bezelScrollableList(listState)
        ) {
            // One UI Watch Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                ) {
                    Text(
                        text = "5D Ab",
                        color = OneUiHealthGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (totalSessions > 0) {
                            "$streakDays day streak • $totalSessions workouts"
                        } else {
                            "Weekly Routines"
                        },
                        color = OneUiTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // One UI Pill Cards for Each Day (Mon, Tue, Wed, Thu, Fri)
            items(routines, key = { it.id }) { routine ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(OneUiCardSurface)
                        .clickable { onSelectRoutine(routine.id) }
                        .padding(start = 14.dp, end = 8.dp, top = 9.dp, bottom = 9.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = routine.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = routine.subtitle,
                            color = OneUiHealthGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${routine.steps.size} sets • Tap to edit",
                            color = OneUiTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { onQuickStartRoutine(routine.id) },
                        colors = ButtonDefaults.primaryButtonColors(
                            backgroundColor = OneUiHealthGreen,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start ${routine.name}",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // New Custom Routine
            item {
                Chip(
                    onClick = onCreateCustomRoutine,
                    label = {
                        Text(
                            text = "New Custom Routine",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    icon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(22.dp)
                                .background(OneUiHealthGreen.copy(alpha = 0.18f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = OneUiHealthGreen,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors(
                        backgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // History
            item {
                Chip(
                    onClick = onOpenHistory,
                    label = {
                        Text(
                            text = "Workout History",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = WatchIcons.History,
                            contentDescription = null,
                            tint = OneUiBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(
                        backgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Settings
            item {
                Chip(
                    onClick = onOpenSettings,
                    label = {
                        Text(
                            text = "Settings",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = OneUiTextSecondary,
                            modifier = Modifier.size(18.dp)
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
