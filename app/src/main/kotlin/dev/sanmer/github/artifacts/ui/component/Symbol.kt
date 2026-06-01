package dev.sanmer.github.artifacts.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Dot(
    modifier: Modifier,
    color: Color
) = Canvas(modifier = modifier) {
    val radius = size.minDimension / 2f

    drawCircle(
        color = color,
        radius = radius,
        center = center
    )
}

@Composable
fun X(
    modifier: Modifier,
    color: Color,
    strokeWidth: Dp = 2.dp,
) = Canvas(modifier = modifier) {
    val strokePx = strokeWidth.toPx()
    val insetPx = strokePx / 2f

    val right = size.width - insetPx
    val bottom = size.height - insetPx

    drawLine(
        color = color,
        start = Offset(insetPx, insetPx),
        end = Offset(right, bottom),
        strokeWidth = strokePx,
        cap = StrokeCap.Round
    )

    drawLine(
        color = color,
        start = Offset(right, insetPx),
        end = Offset(insetPx, bottom),
        strokeWidth = strokePx,
        cap = StrokeCap.Round
    )
}