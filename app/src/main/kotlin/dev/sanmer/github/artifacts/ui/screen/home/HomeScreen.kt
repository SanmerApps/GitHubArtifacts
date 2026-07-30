package dev.sanmer.github.artifacts.ui.screen.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import dev.sanmer.github.artifacts.ui.ktx.isScrollingUp
import dev.sanmer.github.artifacts.ui.screen.Screen
import dev.sanmer.github.artifacts.ui.screen.home.component.RepoList

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    goTo: (Screen) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isScrollingUp by viewModel.listState.isScrollingUp()

    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                onClick = { goTo(Screen.Token) },
                visible = isScrollingUp
            )
        }
    ) { contentPadding ->
        viewModel.data.onSuccess { list ->
            RepoList(
                list = list,
                state = viewModel.listState,
                update = viewModel::update,
                onUpdate = viewModel::update,
                onClick = { token, repo -> goTo(Screen.Workflow(token.token, repo)) },
                contentPadding = contentPadding,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(R.string.launch_name)) },
    actions = {
        val context = LocalContext.current
        IconButton(
            onClick = {
                context.startActivity(
                    Intent.parseUri(Const.GITHUB_URL, Intent.URI_INTENT_SCHEME)
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.brand_github),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    visible: Boolean = true
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + scaleIn(),
    exit = scaleOut() + fadeOut()
) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.key),
            contentDescription = null
        )
    }
}
