package dev.sanmer.github.artifacts.ui.di

import androidx.navigation3.runtime.NavBackStack
import dev.sanmer.github.artifacts.ui.screen.Screen
import dev.sanmer.github.artifacts.ui.screen.home.HomeScreen
import dev.sanmer.github.artifacts.ui.screen.repo.AddRepoScreen
import dev.sanmer.github.artifacts.ui.screen.repo.RepoScreen
import dev.sanmer.github.artifacts.ui.screen.setting.SettingScreen
import dev.sanmer.github.artifacts.ui.screen.token.AddTokenScreen
import dev.sanmer.github.artifacts.ui.screen.token.TokenScreen
import dev.sanmer.github.artifacts.ui.screen.workflow.WorkflowScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val Navigation = module {
    includes(ViewModels)

    activityRetainedScope {
        scoped { NavBackStack(Screen.Home) }

        navigation<Screen.Home> {
            val backStack = get<NavBackStack<Screen>>()
            HomeScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add
            )
        }

        navigation<Screen.Workflow> {
            val backStack = get<NavBackStack<Screen>>()
            WorkflowScreen(
                viewModel = koinViewModel { parametersOf(it.token, it.owner, it.name) },
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Setting> {
            val backStack = get<NavBackStack<Screen>>()
            SettingScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Token> {
            val backStack = get<NavBackStack<Screen>>()
            TokenScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.AddToken> {
            val backStack = get<NavBackStack<Screen>>()
            AddTokenScreen(
                viewModel = koinViewModel { parametersOf(it.token) },
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Repo> {
            val backStack = get<NavBackStack<Screen>>()
            RepoScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.AddRepo> {
            val backStack = get<NavBackStack<Screen>>()
            AddRepoScreen(
                viewModel = koinViewModel { parametersOf(it.id) },
                goBack = backStack::removeLastOrNull
            )
        }
    }
}