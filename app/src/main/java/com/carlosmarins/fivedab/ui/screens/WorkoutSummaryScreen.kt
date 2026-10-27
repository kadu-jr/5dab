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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.carlosmarins.fivedab.data.CompletedWorkoutSession
import com.carlosmarins.fivedab.ui.components.FiveDPentagonRadar
import com.carlosmarins.fivedab.ui.components.bezelScrollableList
import com.carlosmarins.fivedab.ui.theme.WatchAmber
import com.carlosmarins.fivedab.ui.theme.WatchCoral
import com.carlosmarins.fivedab.ui.theme.WatchCyan
import com.carlosmarins.fivedab.ui.theme.WatchEmerald
import com.carlosmarins.fivedab.ui.theme.WatchSurface
import com.carlosmarins.fivedab.ui.theme.WatchTextSecondary
import java.util.Locale

@Composable
fun WorkoutSummaryScreen(
    session: CompletedWorkoutSession,
    onDone: () -> Unit
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FiveDPentagonRadar(
                        activeDimensions = session.dimensionsTrained,
                        size = 46.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "WORKOUT COMPLETE",
                        color = WatchEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = session.routineName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Stats Grid Row 1: Duration & Calories
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        label = "DURATION",
                        value = formatMinutesSeconds(session.activeDurationSeconds),
                        accent = WatchCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "CALORIES",
                        value = String.format(Locale.US, "%.0f kcal", session.caloriesBurnedKcal),
                        accent = WatchAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stats Grid Row 2: Total Reps & Hold Seconds
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        label = "TOTAL REPS",
                        value = "${session.totalRepsCompleted}",
                        accent = WatchEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "PLANK / HOLD",
                        value = "${session.totalHoldSecondsCompleted}s",
                        accent = WatchCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stats Grid Row 3: Avg & Max Heart Rate
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        label = "AVG HR",
                        value = if (session.avgHeartRateBpm > 0) "${session.avgHeartRateBpm} bpm" else "--",
                        accent = WatchCoral,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "MAX HR",
                        value = if (session.maxHeartRateBpm > 0) "${session.maxHeartRateBpm} bpm" else "--",
                        accent = WatchCoral,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Samsung Health / Health Connect Sync Badge
            item {
                Text(
                    text = if (session.syncedToHealth) {
                        "✓ Synced with Samsung Health / Health Services"
                    } else {
                        "Saved locally on Watch"
                    },
                    color = if (session.syncedToHealth) WatchEmerald else WatchTextSecondary,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Done Button
            item {
                Chip(
                    onClick = onDone,
                    label = {
                        Text(
                            text = "Done",
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
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(WatchSurface, RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp)
    ) {
        Text(
            text = label,
            color = WatchTextSecondary,
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            color = accent,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
