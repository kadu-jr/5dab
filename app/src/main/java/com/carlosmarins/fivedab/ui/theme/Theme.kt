package com.carlosmarins.fivedab.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import com.carlosmarins.fivedab.data.AbDimension

val WatchCyan = Color(0xFF00E5FF)
val WatchEmerald = Color(0xFF00E676)
val WatchAmber = Color(0xFFFFB300)
val WatchCoral = Color(0xFFFF5252)
val WatchPurple = Color(0xFFE040FB)
val WatchMint = Color(0xFF64FFDA)
val WatchSurface = Color(0xFF141C26)
val WatchSurfaceVariant = Color(0xFF1E2938)
val WatchTextSecondary = Color(0xFFA6B4C6)

private val FiveDAbColorPalette = Colors(
    primary = WatchCyan,
    primaryVariant = Color(0xFF00B8D4),
    secondary = WatchEmerald,
    secondaryVariant = Color(0xFF00C853),
    background = Color.Black,
    surface = WatchSurface,
    error = WatchCoral,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = WatchTextSecondary,
    onError = Color.Black
)

fun AbDimension.accentColor(): Color = Color(this.colorHex)

@Composable
fun FiveDAbWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = FiveDAbColorPalette,
        content = content
    )
}
