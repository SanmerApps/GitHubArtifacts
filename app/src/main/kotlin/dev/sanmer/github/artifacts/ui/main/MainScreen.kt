package dev.sanmer.github.artifacts.ui.main

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.sanmer.github.artifacts.ui.screen.home.HomeScreen
import dev.sanmer.github.artifacts.ui.screen.repo.EditRepoScreen
import dev.sanmer.github.artifacts.ui.screen.repo.RepoScreen
import dev.sanmer.github.artifacts.ui.screen.setting.SettingScreen
import dev.sanmer.github.artifacts.ui.screen.token.EditTokenScreen
import dev.sanmer.github.artifacts.ui.screen.token.TokenScreen
import dev.sanmer.github.artifacts.ui.screen.workflow.WorkflowScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        navController = navController,
        startDestination = Screen.Home()
    ) {
        Screen.Home(navController).addTo(this)
        Screen.Workflow(navController).addTo(this)
        Screen.Setting(navController).addTo(this)
        Screen.Token(navController).addTo(this)
        Screen.EditToken(navController).addTo(this)
        Screen.Repo(navController).addTo(this)
        Screen.EditRepo(navController).addTo(this)
    }
}

sealed class Screen(
    private val route: String,
    private val content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
    private val arguments: List<NamedNavArgument> = emptyList(),
    private val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = { fadeIn() },
    private val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = { fadeOut() },
) {
    fun addTo(builder: NavGraphBuilder) = builder.composable(
        route = this@Screen.route,
        arguments = this@Screen.arguments,
        enterTransition = this@Screen.enterTransition,
        exitTransition = this@Screen.exitTransition,
        content = this@Screen.content
    )

    @Suppress("FunctionName")
    companion object Routes {
        fun Home() = "Home"
        fun Workflow(id: Any) = "Workflow/${id}"
        fun Setting() = "Setting"
        fun Token() = "Token"
        fun EditToken(token: String = " ") = "EditToken/${token}"
        fun Repo() = "Repo"
        fun EditRepo(id: Any = 0L) = "EditRepo/${id}"
    }

    class Home(navController: NavController) : Screen(
        route = Home(),
        content = { HomeScreen(navController = navController) }
    )

    class Workflow(navController: NavController) : Screen(
        route = Workflow("{id}"),
        content = { WorkflowScreen(navController = navController) },
        arguments = listOf(
            navArgument("id") { type = NavType.LongType }
        )
    )

    class Setting(navController: NavController) : Screen(
        route = Setting(),
        content = { SettingScreen(navController = navController) }
    )

    class Token(navController: NavController) : Screen(
        route = Token(),
        content = { TokenScreen(navController = navController) }
    )

    class EditToken(navController: NavController) : Screen(
        route = EditToken("{token}"),
        content = { EditTokenScreen(navController = navController) },
        arguments = listOf(
            navArgument("token") { type = NavType.StringType }
        )
    )

    class Repo(navController: NavController) : Screen(
        route = Repo(),
        content = { RepoScreen(navController = navController) }
    )

    class EditRepo(navController: NavController) : Screen(
        route = EditRepo("{id}"),
        content = { EditRepoScreen(navController = navController) },
        arguments = listOf(
            navArgument("id") { type = NavType.LongType }
        )
    )
}