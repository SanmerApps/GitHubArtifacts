package dev.sanmer.github.artifacts.ui.ktx

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified

@Stable
fun Modifier.surface(
    shape: Shape,
    backgroundColor: Color,
    border: BorderStroke? = null,
    shadowElevation: Dp = Dp.Unspecified,
) = then(
    if (shadowElevation.isSpecified) Modifier.shadow(
        elevation = shadowElevation,
        shape = shape
    ) else Modifier
)
    .then(if (border != null) Modifier.border(border = border, shape = shape) else Modifier)
    .background(color = backgroundColor, shape = shape)
    .clip(shape = shape)