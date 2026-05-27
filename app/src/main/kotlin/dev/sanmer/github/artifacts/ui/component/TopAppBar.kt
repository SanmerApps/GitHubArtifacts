package dev.sanmer.github.artifacts.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

@Composable
fun appBarContainerColor(
    scrollBehavior: TopAppBarScrollBehavior,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    scrolledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer
): State<Color> {
    val targetColor by remember(scrollBehavior) {
        derivedStateOf {
            val overlappingFraction = scrollBehavior.state.overlappedFraction
            lerp(
                containerColor,
                scrolledContainerColor,
                FastOutLinearInEasing.transform(if (overlappingFraction > 0.01f) 1f else 0f)
            )
        }
    }
    return animateColorAsState(targetColor)
}