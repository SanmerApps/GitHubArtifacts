package dev.sanmer.github.artifacts.ui.screen.token

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.PageIndicator
import dev.sanmer.github.artifacts.ui.ktx.isScrollingUp
import dev.sanmer.github.artifacts.ui.ktx.navigateSingleTopTo
import dev.sanmer.github.artifacts.ui.main.Screen
import dev.sanmer.github.artifacts.ui.screen.token.component.TokenList
import dev.sanmer.github.artifacts.viewmodel.TokenViewModel

@Composable
fun TokenScreen(
    viewModel: TokenViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
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
                .fillMaxSize()
        ) {
            if (viewModel.loadData.isCompleted && viewModel.tokens.isEmpty()) {
                PageIndicator(
                    icon = R.drawable.key,
                    text = R.string.token_empty,
                    modifier = Modifier.padding(contentPadding)
                )
            }

            TokenList(
                tokens = viewModel.tokens,
                onDelete = viewModel::delete,
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
        onClick = { navController.navigateSingleTopTo(Screen.EditToken("", false)) }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.pencil_plus),
            contentDescription = null
        )
    }
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.settings_token_title)) },
    navigationIcon = {
        IconButton(
            onClick = { navController.navigateUp() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)