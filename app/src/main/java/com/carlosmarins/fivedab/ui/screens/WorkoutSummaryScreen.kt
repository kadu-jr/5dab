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
import com.carlosmarins.fivedab.ui.components.bezelScrollableList
import com.carlosmarins.fivedab.ui.theme.OneUiBlue
import com.carlosmarins.fivedab.ui.theme.OneUiCalorieOrange
import com.carlosmarins.fivedab.ui.theme.OneUiCardSurface
import com.carlosmarins.fivedab.ui.theme.OneUiHealthGreen
import com.carlosmarins.fivedab.ui.theme.OneUiHeartRed
import com.carlosmarins.fivedab.ui.theme.OneUiTextSecondary
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
                    Text(
                        text = "Treino Concluído",
                        color = OneUiHealthGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = session.routineName,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Stats Grid Row 1: Duração & Calorias
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        label = "DURAÇÃO",
                        value = formatMinutesSeconds(session.activeDurationSeconds),
                        accent = OneUiHealthGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "CALORIAS",
                        value = String.format(Locale.US, "%.0f kcal", session.caloriesBurnedKcal),
                        accent = OneUiCalorieOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stats Grid Row 2: Repetições & Isometria
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        label = "REPETIÇÕES",
                        value = "${session.totalRepsCompleted}",
                        accent = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "PRANCHA",
                        value = "${session.totalHoldSecondsCompleted}s",
                        accent = OneUiBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stats Grid Row 3: FC Média & FC Máxima
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        label = "FC MÉDIA",
                        value = if (session.avgHeartRateBpm > 0) "${session.avgHeartRateBpm} bpm" else "--",
                        accent = OneUiHeartRed,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "FC MÁX",
                        value = if (session.maxHeartRateBpm > 0) "${session.maxHeartRateBpm} bpm" else "--",
                        accent = OneUiHeartRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Samsung Health / Health Connect Sync Badge
            item {
                Text(
                    text = if (session.syncedToHealth) {
                        "✓ Sincronizado com Samsung Health"
                    } else {
                        "Salvo no relógio"
                    },
                    color = if (session.syncedToHealth) OneUiHealthGreen else OneUiTextSecondary,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botão Concluir
            item {
                Chip(
                    onClick = onDone,
                    label = {
                        Text(
                            text = "Concluir",
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
                        backgroundColor = OneUiHealthGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(50),
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
            .background(OneUiCardSurface, RoundedCornerShape(16.dp))
            .padding(vertical = 7.dp, horizontal = 4.dp)
    ) {
        Text(
            text = label,
            color = OneUiTextSecondary,
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = accent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
