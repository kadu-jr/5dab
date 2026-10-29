package com.carlosmarins.fivedab.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.carlosmarins.fivedab.ui.theme.OneUiCardSurface
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularWorkoutRing(
    setProgressFraction: Float,
    overallRoutineFraction: Float,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedSetProgress by animateFloatAsState(
        targetValue = setProgressFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 250),
        label = "setProgress"
    )
    val animatedOverallProgress by animateFloatAsState(
        targetValue = overallRoutineFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 350),
        label = "overallProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val outerStroke = 5.5.dp.toPx()
        val innerStroke = 2.dp.toPx()
        val outerInset = outerStroke / 2f + 2.dp.toPx()
        val innerInset = outerInset + outerStroke + 2.5.dp.toPx()

        // Samsung Health style dark neutral outer track
        drawArc(
            color = OneUiCardSurface,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(outerInset, outerInset),
            size = Size(size.width - outerInset * 2, size.height - outerInset * 2),
            style = Stroke(width = outerStroke, cap = StrokeCap.Round)
        )

        // Active Samsung Health progress arc
        val sweep = 360f * animatedSetProgress
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(outerInset, outerInset),
            size = Size(size.width - outerInset * 2, size.height - outerInset * 2),
            style = Stroke(width = outerStroke, cap = StrokeCap.Round)
        )

        // Signature One UI Watch / Samsung Health endpoint indicator dot
        if (animatedSetProgress > 0.01f) {
            val angleRad = Math.toRadians((-90.0 + sweep.toDouble()))
            val radius = (size.minDimension / 2f) - outerInset
            val cx = (size.width / 2f) + (radius * cos(angleRad)).toFloat()
            val cy = (size.height / 2f) + (radius * sin(angleRad)).toFloat()
            drawCircle(
                color = Color.White,
                radius = outerStroke * 0.42f,
                center = Offset(cx, cy)
            )
        }

        // Subtle inner progress line for overall routine completion
        drawArc(
            color = Color.White.copy(alpha = 0.08f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(innerInset, innerInset),
            size = Size(size.width - innerInset * 2, size.height - innerInset * 2),
            style = Stroke(width = innerStroke, cap = StrokeCap.Round)
        )
        drawArc(
            color = primaryColor.copy(alpha = 0.45f),
            startAngle = -90f,
            sweepAngle = 360f * animatedOverallProgress,
            useCenter = false,
            topLeft = Offset(innerInset, innerInset),
            size = Size(size.width - innerInset * 2, size.height - innerInset * 2),
            style = Stroke(width = innerStroke, cap = StrokeCap.Round)
        )
    }
}
