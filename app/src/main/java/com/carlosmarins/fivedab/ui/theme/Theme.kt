package com.carlosmarins.fivedab.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import com.carlosmarins.fivedab.data.AbDimension

// Samsung One UI Watch & Samsung Health Official Palette
val OneUiHealthGreen = Color(0xFF2CE59B)
val OneUiBlue = Color(0xFF4CA2FF)
val OneUiHeartRed = Color(0xFFFF4D5E)
val OneUiCalorieOrange = Color(0xFFFF9F1C)
val OneUiCardSurface = Color(0xFF1F2024)
val OneUiButtonSurface = Color(0xFF2A2C32)
val OneUiTextSecondary = Color(0xFF9E9FA5)

// Aliases for existing screen references mapped to One UI Watch tokens
val WatchCyan = OneUiBlue
val WatchEmerald = OneUiHealthGreen
val WatchAmber = OneUiCalorieOrange
val WatchCoral = OneUiHeartRed
val WatchPurple = Color(0xFFA970FF)
val WatchMint = OneUiHealthGreen
val WatchSurface = OneUiCardSurface
val WatchSurfaceVariant = OneUiButtonSurface
val WatchTextSecondary = OneUiTextSecondary

private val OneUiWatchColorPalette = Colors(
    primary = OneUiHealthGreen,
    primaryVariant = Color(0xFF1CB879),
    secondary = OneUiBlue,
    secondaryVariant = Color(0xFF387AFF),
    background = Color.Black,
    surface = OneUiCardSurface,
    error = OneUiHeartRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = OneUiTextSecondary,
    onError = Color.Black
)

fun AbDimension.accentColor(): Color = Color(this.colorHex)

@Composable
fun FiveDAbWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = OneUiWatchColorPalette,
        content = content
    )
}
