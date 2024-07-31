package dev.sanmer.github.artifacts.ui.screen.home.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.github.artifacts.ui.ktx.navigateSingleTopTo
import dev.sanmer.github.artifacts.ui.main.Screen
import dev.sanmer.github.response.Repository

@Composable
fun RepoList(
    repos: List<Repository>,
    navController: NavController,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(),
    state = state,
    contentPadding = contentPadding,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val size = repos.size
    itemsIndexed(repos) { index, repo ->
        RepoItem(
            repo = repo,
            onClick = {
                navController.navigateSingleTopTo(
                    Screen.Workflow(repo.id)
                )
            }
        )

        ListDivider(
            index = index,
            size = size
        )
    }
}

@Composable
fun ListDivider(
    index: Int,
    size: Int
) = if (index < size - 1) {
    HorizontalDivider()
} else {
}