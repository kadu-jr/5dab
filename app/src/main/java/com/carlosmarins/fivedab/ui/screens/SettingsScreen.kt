package com.carlosmarins.fivedab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.carlosmarins.fivedab.data.WorkoutSettings
import com.carlosmarins.fivedab.ui.components.WatchIcons
import com.carlosmarins.fivedab.ui.components.bezelScrollableList
import com.carlosmarins.fivedab.ui.theme.OneUiBlue
import com.carlosmarins.fivedab.ui.theme.OneUiButtonSurface
import com.carlosmarins.fivedab.ui.theme.OneUiCardSurface
import com.carlosmarins.fivedab.ui.theme.OneUiTextSecondary
import java.util.Locale

@Composable
fun SettingsScreen(
    settings: WorkoutSettings,
    onToggleAutoPace: () -> Unit,
    onToggleHaptics: () -> Unit,
    onToggleAudioTones: () -> Unit,
    onToggleVoiceCoach: () -> Unit,
    onCycleBezelMode: () -> Unit,
    onAdjustWeight: (Float) -> Unit
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
                    text = "Configurações",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ToggleChip(
                    checked = settings.autoPaceReps,
                    onCheckedChange = { onToggleAutoPace() },
                    label = {
                        Column {
                            Text("Ritmo Automático", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Conta reps pelo temporizador",
                                fontSize = 8.sp,
                                color = OneUiTextSecondary
                            )
                        }
                    },
                    toggleControl = {
                        Switch(checked = settings.autoPaceReps)
                    },
                    colors = ToggleChipDefaults.toggleChipColors(
                        checkedStartBackgroundColor = OneUiCardSurface,
                        checkedEndBackgroundColor = OneUiCardSurface,
                        uncheckedStartBackgroundColor = OneUiCardSurface,
                        uncheckedEndBackgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ToggleChip(
                    checked = settings.hapticEnabled,
                    onCheckedChange = { onToggleHaptics() },
                    label = {
                        Column {
                            Text("Vibração no Pulso", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Vibra a cada repetição e alerta",
                                fontSize = 8.sp,
                                color = OneUiTextSecondary
                            )
                        }
                    },
                    toggleControl = {
                        Switch(checked = settings.hapticEnabled)
                    },
                    colors = ToggleChipDefaults.toggleChipColors(
                        checkedStartBackgroundColor = OneUiCardSurface,
                        checkedEndBackgroundColor = OneUiCardSurface,
                        uncheckedStartBackgroundColor = OneUiCardSurface,
                        uncheckedEndBackgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ToggleChip(
                    checked = settings.voiceCoachEnabled,
                    onCheckedChange = { onToggleVoiceCoach() },
                    label = {
                        Column {
                            Text("Guia de Voz (TTS)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Anuncia exercícios e contagem",
                                fontSize = 8.sp,
                                color = OneUiTextSecondary
                            )
                        }
                    },
                    toggleControl = {
                        Switch(checked = settings.voiceCoachEnabled)
                    },
                    colors = ToggleChipDefaults.toggleChipColors(
                        checkedStartBackgroundColor = OneUiCardSurface,
                        checkedEndBackgroundColor = OneUiCardSurface,
                        uncheckedStartBackgroundColor = OneUiCardSurface,
                        uncheckedEndBackgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ToggleChip(
                    checked = settings.audioTonesEnabled,
                    onCheckedChange = { onToggleAudioTones() },
                    label = {
                        Column {
                            Text("Bipes Sonoros", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Sons de cadência e contagem 3-2-1",
                                fontSize = 8.sp,
                                color = OneUiTextSecondary
                            )
                        }
                    },
                    toggleControl = {
                        Switch(checked = settings.audioTonesEnabled)
                    },
                    colors = ToggleChipDefaults.toggleChipColors(
                        checkedStartBackgroundColor = OneUiCardSurface,
                        checkedEndBackgroundColor = OneUiCardSurface,
                        uncheckedStartBackgroundColor = OneUiCardSurface,
                        uncheckedEndBackgroundColor = OneUiCardSurface
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Chip(
                    onClick = onCycleBezelMode,
                    label = {
                        Column {
                            Text(
                                text = "Coroa Giratória (Watch8)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = settings.bezelWorkoutAction.label,
                                fontSize = 9.sp,
                                color = OneUiBlue
                            )
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors(backgroundColor = OneUiCardSurface),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Peso Corporal (Cálculo kcal)",
                        color = OneUiTextSecondary,
                        fontSize = 9.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { onAdjustWeight(-1f) },
                            colors = ButtonDefaults.secondaryButtonColors(backgroundColor = OneUiButtonSurface),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = WatchIcons.Remove,
                                contentDescription = "Diminuir peso",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = String.format(Locale.US, "%.0f kg", settings.userWeightKg),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = { onAdjustWeight(1f) },
                            colors = ButtonDefaults.secondaryButtonColors(backgroundColor = OneUiButtonSurface),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Aumentar peso",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
