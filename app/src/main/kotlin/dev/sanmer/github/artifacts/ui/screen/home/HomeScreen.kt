package dev.sanmer.github.artifacts.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.sanmer.github.artifacts.Const
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ktx.viewUrl
import dev.sanmer.github.artifacts.ui.component.Finished
import dev.sanmer.github.artifacts.ui.ktx.isScrollingUp
import dev.sanmer.github.artifacts.ui.screen.Screen
import dev.sanmer.github.artifacts.ui.screen.home.component.RepoList

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    goTo: (Screen) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isScrollingUp,
                enter = fadeIn() + scaleIn(),
                exit = scaleOut() + fadeOut()
            ) {
                ActionButton(
                    onToken = { goTo(Screen.Token) }
                )
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
        ) {
            if (viewModel.loadData.isSuccess && viewModel.list.isEmpty()) {
                Finished(
                    label = R.string.repo_empty,
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                )
            }

            RepoList(
                list = viewModel.list,
                update = viewModel::update,
                onUpdate = viewModel::update,
                onClick = { goTo(Screen.Workflow(it)) },
                state = listState,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.launch_name)) },
    actions = {
        val context = LocalContext.current
        IconButton(
            onClick = { context.viewUrl(Const.GITHUB_URL) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.brand_github),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun ActionButton(
    onToken: () -> Unit
) = FloatingActionButton(
    onClick = onToken
) {
    Icon(
        painter = painterResource(id = R.drawable.key),
        contentDescription = null
    )
}
