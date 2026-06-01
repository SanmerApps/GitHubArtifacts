package dev.sanmer.github.artifacts.ui.ktx

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.Dp

fun CornerBasedShape.top(size: Dp) =
    copy(topStart = CornerSize(size), topEnd = CornerSize(size))

fun CornerBasedShape.bottom(size: Dp) =
    copy(bottomStart = CornerSize(size), bottomEnd = CornerSize(size))