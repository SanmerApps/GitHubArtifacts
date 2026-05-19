package dev.sanmer.github.artifacts.ui.screen.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.Logo

@Composable
fun RepoList(
    list: List<RepoEntity.AndToken>,
    update: (RepoEntity) -> LoadData<Unit>,
    onUpdate: (RepoEntity, TokenEntity) -> Unit,
    onClick: (RepoEntity) -> Unit,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(),
    state = state,
    contentPadding = contentPadding,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(5.dp)
) {
    items(list) { (repo, token) ->
        RepoItem(
            repo = repo,
            update = update(repo),
            onUpdate = { onUpdate(repo, token) },
            onClick = { onClick(repo) }
        )
    }
}

@Composable
private fun RepoItem(
    repo: RepoEntity,
    update: LoadData<Unit>,
    onUpdate: () -> Unit,
    onClick: () -> Unit
) = Row(
    modifier = Modifier
        .clip(shape = MaterialTheme.shapes.medium)
        .clickable(onClick = if (update.isSuccess) onClick else onUpdate)
        .padding(horizontal = 15.dp, vertical = 10.dp)
        .fillMaxWidth(),
    verticalAlignment = Alignment.Top
) {
    RepoItem(
        repo = repo,
        modifier = Modifier.weight(1f)
    )

    AnimatedContent(
        targetState = update,
        transitionSpec = { (fadeIn() + scaleIn()) togetherWith (scaleOut() + fadeOut()) }
    ) {
        when (it) {
            LoadData.Loading -> CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )

            is LoadData.Failure -> Logo(
                icon = R.drawable.x,
                modifier = Modifier.size(24.dp),
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                containerColor = MaterialTheme.colorScheme.errorContainer
            )

            else -> Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

