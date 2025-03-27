package dev.sanmer.github.artifacts.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.PageIndicator
import dev.sanmer.github.artifacts.ui.ktx.isScrollingUp
import dev.sanmer.github.artifacts.ui.ktx.navigateSingleTopTo
import dev.sanmer.github.artifacts.ui.main.Screen
import dev.sanmer.github.artifacts.ui.screen.home.component.RepoList
import dev.sanmer.github.artifacts.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val repos by viewModel.repos.collectAsStateWithLifecycle(initialValue = emptyList())
    val progress by viewModel.progressFlow.collectAsStateWithLifecycle(initialValue = -1f)

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

    Scaffold(
        topBar = {
            TopBar(
                progress = progress,
                onRefresh = viewModel::updateRepoAll,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isScrollingUp,
                enter = fadeIn() + scaleIn(),
                exit = scaleOut() + fadeOut()
            ) {
                ActionButton(navController = navController)
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (repos.isEmpty()) {
                PageIndicator(
                    icon = R.drawable.git_branch,
                    text = R.string.repo_empty,
                    modifier = Modifier.padding(contentPadding)
                )
            }

            RepoList(
                repos = repos,
                navController = navController,
                state = listState,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
private fun ActionButton(
    navController: NavController
) {
    FloatingActionButton(
        onClick = { navController.navigateSingleTopTo(Screen.Setting()) }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.settings_2),
            contentDescription = null
        )
    }
}

@Composable
private fun TopBar(
    progress: Float,
    onRefresh: () ->  Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.launch_name)) },
    actions = {
        IconButton(
            onClick = onRefresh,
        ) {
            val animatedScale by animateFloatAsState(
                targetValue = if (progress >= 0) 0.65f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )

            AnimatedVisibility(
                visible = progress >= 0,
                enter = fadeIn() + scaleIn(),
                exit = scaleOut() + fadeOut()
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.refresh),
                contentDescription = null,
                modifier = Modifier.scale(animatedScale)
            )
        }
    },
    scrollBehavior = scrollBehavior
)