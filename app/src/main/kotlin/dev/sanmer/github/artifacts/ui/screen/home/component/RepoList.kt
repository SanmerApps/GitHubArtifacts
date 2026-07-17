package dev.sanmer.github.artifacts.ui.screen.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.database.model.Token
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.AnimatedPoint
import dev.sanmer.github.artifacts.ui.component.Point

@Composable
fun RepoList(
    list: List<Repo.AndToken>,
    update: (Repo) -> LoadData<Unit>,
    onUpdate: (Repo, Token) -> Unit,
    onClick: (Token, Repo) -> Unit,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(),
    state = state,
    contentPadding = contentPadding
) {
    items(
        items = list,
        key = { it.repo.id }
    ) { (repo, token) ->
        RepoItem(
            repo = repo,
            update = update(repo),
            onUpdate = { onUpdate(repo, token) },
            onClick = { onClick(token, repo) }
        )
    }
}

@Composable
private fun RepoItem(
    repo: Repo,
    update: LoadData<Unit>,
    onUpdate: () -> Unit,
    onClick: () -> Unit
) = Row(
    modifier = Modifier
        .clip(shape = MaterialTheme.shapes.medium)
        .clickable(onClick = if (update.isSuccess) onClick else onUpdate)
        .padding(all = 15.dp)
        .fillMaxWidth()
) {
    RepoItem(
        repo = repo,
        modifier = Modifier.weight(1f)
    )

    AnimatedContent(
        targetState = update,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentAlignment = Alignment.Center
    ) {
        when (it) {
            LoadData.Loading -> AnimatedPoint(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            )

            is LoadData.Failure -> Point(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.errorContainer
            )

            else -> Spacer(
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

