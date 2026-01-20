package dev.sanmer.github.artifacts.ui.screen.repo.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.RepoWithToken
import dev.sanmer.github.artifacts.ui.ktx.surface
import dev.sanmer.github.artifacts.ui.screen.home.component.Title
import dev.sanmer.github.artifacts.ui.screen.home.component.Value
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun RepoItem(
    repo: RepoWithToken,
    onClick: () -> Unit
) = Column(
    modifier = Modifier
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .clickable(onClick = onClick)
        .padding(all = 15.dp)
        .fillMaxWidth()
) {
    Title(
        title = repo.repo.fullName,
        subtitle = repo.repo.state()
    )

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.outline
    ) {
        BottomRow(repo = repo)
    }
}

@Composable
private fun RepoEntity.state(): String {
    return if (private) {
        when {
            archived -> stringResource(id = R.string.repo_private_archive)
            isTemplate -> stringResource(id = R.string.repo_private_template)
            else -> stringResource(id = R.string.repo_private)
        }
    } else {
        when {
            archived -> stringResource(id = R.string.repo_public_archive)
            isTemplate -> stringResource(id = R.string.repo_public_template)
            else -> stringResource(id = R.string.repo_public)
        }
    }
}

@Composable
private fun BottomRow(
    repo: RepoWithToken
) = FlowRow(
    modifier = Modifier.padding(top = 5.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(5.dp)
) {
    val updatedAt by remember {
        derivedStateOf {
            repo.repo.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Value(
        icon = R.drawable.key,
        value = repo.token.name
    )

    Value(value = updatedAt)
}