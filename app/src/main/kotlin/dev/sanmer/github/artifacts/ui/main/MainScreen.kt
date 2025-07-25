package dev.sanmer.github.artifacts.ui.main

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.ui.screen.home.HomeScreen
import dev.sanmer.github.artifacts.ui.screen.repo.AddRepoScreen
import dev.sanmer.github.artifacts.ui.screen.repo.RepoScreen
import dev.sanmer.github.artifacts.ui.screen.setting.SettingScreen
import dev.sanmer.github.artifacts.ui.screen.token.AddTokenScreen
import dev.sanmer.github.artifacts.ui.screen.token.TokenScreen
import dev.sanmer.github.artifacts.ui.screen.workflow.WorkflowScreen
import kotlinx.serialization.Serializable

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.background
        ),
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                navController = navController
            )
        }

        composable<Screen.Workflow> {
            WorkflowScreen(
                navController = navController
            )
        }

        composable<Screen.Setting> {
            SettingScreen(
                navController = navController
            )
        }

        composable<Screen.Token> {
            TokenScreen(
                navController = navController
            )
        }

        composable<Screen.AddToken> {
            AddTokenScreen(
                navController = navController
            )
        }

        composable<Screen.Repo> {
            RepoScreen(
                navController = navController
            )
        }

        composable<Screen.AddRepo> {
            AddRepoScreen(
                navController = navController
            )
        }
    }
}

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data class Workflow(
        val token: String,
        val owner: String,
        val name: String
    ) : Screen() {
        constructor(entity: RepoEntity) : this(
            token = entity.token,
            owner = entity.owner,
            name = entity.name
        )
    }

    @Serializable
    data object Setting : Screen()

    @Serializable
    data object Token : Screen()

    @Serializable
    data class AddToken(
        val token: String = "-1"
    ) : Screen() {
        val isEdit = token != "-1"
    }

    @Serializable
    data object Repo : Screen()

    @Serializable
    data class AddRepo(
        val id: Long = -1L
    ) : Screen() {
        val isEdit = id != -1L
    }
}