package dev.sanmer.github.artifacts.ui.ktx

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Stable
fun PaddingValues.horizontal() = object : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) =
        this@horizontal.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding() = 0.dp

    override fun calculateRightPadding(layoutDirection: LayoutDirection) =
        this@horizontal.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding() = 0.dp
}

@Stable
fun PaddingValues.vertical() = object : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = 0.dp

    override fun calculateTopPadding() = this@vertical.calculateTopPadding()

    override fun calculateRightPadding(layoutDirection: LayoutDirection) = 0.dp

    override fun calculateBottomPadding() = this@vertical.calculateBottomPadding()
}
