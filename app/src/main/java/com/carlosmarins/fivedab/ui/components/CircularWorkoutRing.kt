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

@Composable
fun CircularWorkoutRing(
    setProgressFraction: Float,
    overallRoutineFraction: Float,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedSetProgress by animateFloatAsState(
        targetValue = setProgressFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 280),
        label = "setProgress"
    )
    val animatedOverallProgress by animateFloatAsState(
        targetValue = overallRoutineFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 400),
        label = "overallProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val outerStroke = 6.dp.toPx()
        val innerStroke = 3.dp.toPx()
        val outerInset = outerStroke / 2f + 2.dp.toPx()
        val innerInset = outerInset + outerStroke + 3.dp.toPx()

        // Outer track background
        drawArc(
            color = primaryColor.copy(alpha = 0.16f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(outerInset, outerInset),
            size = Size(size.width - outerInset * 2, size.height - outerInset * 2),
            style = Stroke(width = outerStroke, cap = StrokeCap.Round)
        )

        // Outer active progress arc (Current Set Reps or Timer)
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = 360f * animatedSetProgress,
            useCenter = false,
            topLeft = Offset(outerInset, outerInset),
            size = Size(size.width - outerInset * 2, size.height - outerInset * 2),
            style = Stroke(width = outerStroke, cap = StrokeCap.Round)
        )

        // Inner track background (Overall Routine Sets)
        drawArc(
            color = Color.White.copy(alpha = 0.10f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(innerInset, innerInset),
            size = Size(size.width - innerInset * 2, size.height - innerInset * 2),
            style = Stroke(width = innerStroke, cap = StrokeCap.Round)
        )

        // Inner active arc (Overall Routine Completion)
        drawArc(
            color = Color.White.copy(alpha = 0.55f),
            startAngle = -90f,
            sweepAngle = 360f * animatedOverallProgress,
            useCenter = false,
            topLeft = Offset(innerInset, innerInset),
            size = Size(size.width - innerInset * 2, size.height - innerInset * 2),
            style = Stroke(width = innerStroke, cap = StrokeCap.Round)
        )
    }
}
