package dev.sanmer.github.artifacts.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun AnimatedLinearWavy(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 4.dp,
    amplitude: Dp = 3.dp,
    wavelength: Dp = 40.dp,
    waveSpeed: Dp = wavelength,
) {
    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }
    val amplitudePx = with(density) { amplitude.toPx() }
    val wavelengthPx = with(density) { wavelength.toPx() }
    val waveSpeedPx = with(density) { waveSpeed.toPx() }

    val transition = rememberInfiniteTransition()
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ((wavelengthPx / waveSpeedPx) * 1000f).toInt().coerceAtLeast(300),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = modifier.height(
            (strokeWidth + amplitude * 2 + 2.dp).coerceAtLeast(8.dp)
        )
    ) {
        val w = size.width
        val centerY = size.height / 2f
        val insetPx = strokePx / 2f
        val k = (2f * PI / wavelengthPx).toFloat()
        fun yAt(x: Float): Float = centerY + amplitudePx * sin(k * x + phase)

        val step = (wavelengthPx / 12f).coerceIn(2f, 8f)
        val pts = arrayListOf<Pair<Float, Float>>()
        var x = insetPx
        while (x < w - insetPx) {
            pts.add(x to yAt(x))
            x += step
        }
        pts.add(x to yAt(x))

        val path = Path().apply { moveTo(pts[0].first, pts[0].second) }
        for (i in 0 until pts.lastIndex) {
            val p0 = if (i - 1 >= 0) pts[i - 1] else pts[i]
            val p1 = pts[i]
            val p2 = pts[i + 1]
            val p3 = if (i + 2 <= pts.lastIndex) pts[i + 2] else pts[i + 1]

            val c1x = p1.first + (p2.first - p0.first) / 6f
            val c1y = p1.second + (p2.second - p0.second) / 6f
            val c2x = p2.first - (p3.first - p1.first) / 6f
            val c2y = p2.second - (p3.second - p1.second) / 6f

            path.cubicTo(c1x, c1y, c2x, c2y, p2.first, p2.second)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun StraightLine(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 4.dp,
) = Canvas(
    modifier = modifier.height(strokeWidth)
) {
    val strokePx = strokeWidth.toPx()
    val insetPx = strokePx / 2f

    drawLine(
        color = color,
        strokeWidth = strokePx,
        start = Offset(insetPx, insetPx),
        end = Offset(size.width - insetPx, insetPx),
        cap = StrokeCap.Round
    )
}