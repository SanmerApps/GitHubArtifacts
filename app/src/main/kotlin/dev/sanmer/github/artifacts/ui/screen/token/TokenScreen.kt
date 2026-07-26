package dev.sanmer.github.artifacts.ui.screen.token

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.model.Token
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.ktx.isScrollingUp
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.screen.Screen
import dev.sanmer.github.artifacts.ui.screen.token.component.TokenItem

@Composable
fun TokenScreen(
    viewModel: TokenViewModel,
    goTo: (Screen) -> Unit,
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

    Scaffold(
        topBar = {
            TopBar(
                onBack = goBack,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                onClick = { goTo(Screen.EditToken()) },
                visible = isScrollingUp
            )
        }
    ) { contentPadding ->
        when (val data = viewModel.data) {
            is LoadData.Success<List<Token.AndRepos>> -> TokenList(
                list = data.value,
                state = listState,
                onClick = { goTo(Screen.EditToken(it.id)) },
                contentPadding = contentPadding,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )

            else -> {}
        }
    }
}

@Composable
private fun TokenList(
    list: List<Token.AndRepos>,
    onClick: (Token) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = modifier,
    state = state,
    contentPadding = contentPadding + PaddingValues(all = 15.dp),
    verticalArrangement = Arrangement.spacedBy(15.dp)
) {
    items(
        items = list,
        key = { it.token.id }
    ) { (token, repos) ->
        TokenItem(
            token = token,
            repos = repos,
            onClick = { onClick(token) }
        )
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(R.string.token_title)) },
    navigationIcon = {
        IconButton(
            onClick = onBack,
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_left),
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
            painter = painterResource(R.drawable.plus),
            contentDescription = null
        )
    }
}