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
import androidx.compose.runtime.Composable
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
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.carlosmarins.fivedab.data.CompletedWorkoutSession
import com.carlosmarins.fivedab.data.WorkoutRepository
import com.carlosmarins.fivedab.ui.components.bezelScrollableList
import com.carlosmarins.fivedab.ui.theme.OneUiCalorieOrange
import com.carlosmarins.fivedab.ui.theme.OneUiCardSurface
import com.carlosmarins.fivedab.ui.theme.OneUiHealthGreen
import com.carlosmarins.fivedab.ui.theme.OneUiTextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(
    history: List<CompletedWorkoutSession>,
    onRequestHealthPermissions: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    val streak = WorkoutRepository.computeStreakDays(history)
    val totalRepsAllTime = history.sumOf { it.totalRepsCompleted }
    val totalKcalAllTime = history.sumOf { it.caloriesBurnedKcal }
    val formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale.US)

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
                        text = "Histórico",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$streak dias seguidos • $totalRepsAllTime reps",
                        color = OneUiHealthGreen,
                        fontSize = 10.sp
                    )
                    Text(
                        text = String.format(Locale.US, "%.0f kcal acumuladas", totalKcalAllTime),
                        color = OneUiTextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            item {
                Chip(
                    onClick = onRequestHealthPermissions,
                    label = {
                        Column {
                            Text(
                                text = "Sincronizar Samsung Health",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Permissões Health Connect",
                                fontSize = 9.sp,
                                color = OneUiTextSecondary
                            )
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors(backgroundColor = OneUiCardSurface),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (history.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum treino registrado ainda.",
                        color = OneUiTextSecondary,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            } else {
                items(history, key = { it.sessionId }) { item ->
                    val dateStr = Instant.ofEpochMilli(item.endTimeEpochMs)
                        .atZone(ZoneId.systemDefault())
                        .format(formatter)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OneUiCardSurface, RoundedCornerShape(18.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = item.routineName,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (item.syncedToHealth) "✓ Health" else "Local",
                                color = if (item.syncedToHealth) OneUiHealthGreen else OneUiTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.totalRepsCompleted} reps • ${item.totalHoldSecondsCompleted}s • ${String.format(Locale.US, "%.0f", item.caloriesBurnedKcal)} kcal",
                            color = OneUiCalorieOrange,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "$dateStr • FC méd ${if (item.avgHeartRateBpm > 0) "${item.avgHeartRateBpm} bpm" else "--"}",
                            color = OneUiTextSecondary,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}
