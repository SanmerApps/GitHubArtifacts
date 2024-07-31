package dev.sanmer.github.artifacts.ui.screen.repo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.RepoWithToken
import dev.sanmer.github.artifacts.ui.component.SwipeContent
import dev.sanmer.github.artifacts.ui.screen.home.component.Title
import dev.sanmer.github.artifacts.ui.screen.home.component.Value
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun RepoItem(
    repo: RepoWithToken,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) = SwipeContent(
    content = { release ->
        RepoButtons(
            onEdit = {
                release()
                onEdit()
            },
            onDelete = {
                release()
                onDelete()
            }
        )
    },
    surface = {
        RepoContent(repo = repo)
    }
)

@Composable
private fun RepoContent(
    repo: RepoWithToken
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium
        )
        .border(
            border = CardDefaults.outlinedCardBorder(),
            shape = MaterialTheme.shapes.medium
        )
        .padding(all = 15.dp)
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

@OptIn(ExperimentalLayoutApi::class)
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

@Composable
private fun RepoButtons(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) = Row(
    modifier = Modifier.padding(horizontal = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(5.dp)
) {
    FilledTonalIconButton(
        onClick = onEdit
    ) {
        Icon(
            painter = painterResource(id = R.drawable.edit),
            contentDescription = null
        )
    }

    FilledTonalIconButton(
        onClick = onDelete,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.trash_x),
            contentDescription = null
        )
    }
}