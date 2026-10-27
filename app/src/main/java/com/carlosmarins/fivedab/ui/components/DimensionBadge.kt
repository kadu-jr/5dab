package com.carlosmarins.fivedab.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.carlosmarins.fivedab.data.AbDimension
import com.carlosmarins.fivedab.ui.theme.WatchCyan
import com.carlosmarins.fivedab.ui.theme.accentColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DimensionBadge(
    dimension: AbDimension,
    modifier: Modifier = Modifier
) {
    val color = dimension.accentColor()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.16f),
                shape = RoundedCornerShape(50)
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.65f),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = "${dimension.code} • ${dimension.shortTitle.uppercase()}",
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FiveDPentagonRadar(
    activeDimensions: Collection<AbDimension>,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val coreFive = listOf(
        AbDimension.FLEXION,
        AbDimension.LATERAL_FLEXION,
        AbDimension.ROTATION,
        AbDimension.COUNTER_EXTENSION,
        AbDimension.COUNTER_LATERAL_FLEXION
    )

    Canvas(modifier = modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val maxRadius = (this.size.minDimension / 2f) * 0.82f

        val outerPath = Path()
        val activePath = Path()

        coreFive.forEachIndexed { idx, dim ->
            val angleRad = Math.toRadians((-90.0 + idx * 72.0))
            val ox = cx + (maxRadius * cos(angleRad)).toFloat()
            val oy = cy + (maxRadius * sin(angleRad)).toFloat()

            if (idx == 0) outerPath.moveTo(ox, oy) else outerPath.lineTo(ox, oy)

            // Draw axis spoke
            drawLine(
                color = dim.accentColor().copy(alpha = 0.28f),
                start = Offset(cx, cy),
                end = Offset(ox, oy),
                strokeWidth = 1.5.dp.toPx()
            )

            val isTrained = dim in activeDimensions
            val r = if (isTrained) maxRadius else maxRadius * 0.30f
            val ax = cx + (r * cos(angleRad)).toFloat()
            val ay = cy + (r * sin(angleRad)).toFloat()

            if (idx == 0) activePath.moveTo(ax, ay) else activePath.lineTo(ax, ay)

            drawCircle(
                color = if (isTrained) dim.accentColor() else dim.accentColor().copy(alpha = 0.30f),
                radius = if (isTrained) 3.2.dp.toPx() else 2.dp.toPx(),
                center = Offset(ax, ay)
            )
        }
        outerPath.close()
        activePath.close()

        drawPath(
            path = outerPath,
            color = WatchCyan.copy(alpha = 0.25f),
            style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = activePath,
            color = WatchCyan.copy(alpha = 0.22f)
        )
        drawPath(
            path = activePath,
            color = WatchCyan,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
