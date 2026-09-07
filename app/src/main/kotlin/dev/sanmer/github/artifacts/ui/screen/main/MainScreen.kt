package dev.sanmer.github.artifacts.ui.screen.main

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.sanmer.github.artifacts.ui.screen.Screen

@Composable
fun MainScreen(
    backStack: NavBackStack<Screen>,
    entryProvider: (Screen) -> NavEntry<Screen>
) {
    NavDisplay(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        backStack = backStack,
        onBack = backStack::removeLastOrNull,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            slideIntoContainer(
                towards = SlideDirection.Start,
                animationSpec = tween(300)
            ) togetherWith fadeOut(
                animationSpec = tween(300)
            ) + slideOutOfContainer(
                towards = SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        popTransitionSpec = {
            slideIntoContainer(
                towards = SlideDirection.End,
                animationSpec = tween(300)
            ) togetherWith fadeOut(
                animationSpec = tween(300)
            ) + slideOutOfContainer(
                towards = SlideDirection.End,
                animationSpec = tween(300)
            )
        },
        predictivePopTransitionSpec = {
            slideIntoContainer(
                towards = SlideDirection.End,
                animationSpec = tween(300)
            ) togetherWith fadeOut(
                animationSpec = tween(300)
            ) + slideOutOfContainer(
                towards = SlideDirection.End,
                animationSpec = tween(300)
            )
        },
        entryProvider = entryProvider
    )
}