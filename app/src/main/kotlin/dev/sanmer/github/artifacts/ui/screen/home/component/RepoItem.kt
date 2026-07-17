package dev.sanmer.github.artifacts.ui.screen.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sanmer.github.Languages
import dev.sanmer.github.artifacts.Const.DATETIME_DISPLAY
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.ktx.format
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

@Composable
fun RepoItem(
    repo: Repo,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
) {
    val pushedAt by remember(repo.id, repo.pushedAt) {
        derivedStateOf {
            repo.pushedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                .format(DATETIME_DISPLAY)
        }
    }

    Title(
        title = repo.fullName,
        subtitle = repo.repoType()
    )

    if (repo.description.isNotBlank()) {
        Text(
            text = repo.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Values(
        repo = repo,
        modifier = Modifier.padding(vertical = 5.dp)
    )

    Value(
        value = pushedAt,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
fun Repo.repoType() = if (private) {
    when {
        archived -> stringResource(R.string.repo_private_archive)
        isTemplate -> stringResource(R.string.repo_private_template)
        else -> stringResource(R.string.repo_private)
    }
} else {
    when {
        archived -> stringResource(R.string.repo_public_archive)
        isTemplate -> stringResource(R.string.repo_public_template)
        else -> stringResource(R.string.repo_public)
    }
}

@Composable
private fun Values(
    repo: Repo,
    modifier: Modifier = Modifier
) = FlowRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
) {
    if (repo.language.isNotBlank()) {
        Value(
            icon = {
                Point(
                    size = 12.dp,
                    color = Color(Languages.color(repo.language))
                )
            },
            value = repo.language,
            color = MaterialTheme.colorScheme.outline
        )
    }

    if (repo.license.isNotEmpty()) {
        Value(
            icon = R.drawable.scale,
            value = repo.license,
            color = MaterialTheme.colorScheme.outline
        )
    }

    Value(
        icon = R.drawable.git_fork,
        value = repo.forksCount.format(),
        color = MaterialTheme.colorScheme.outline
    )

    Value(
        icon = R.drawable.star,
        value = repo.stargazersCount.format(),
        color = MaterialTheme.colorScheme.outline
    )

    if (repo.hasIssues) {
        Value(
            icon = R.drawable.circle_dot,
            value = repo.openIssuesCount.format(),
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun Point(
    size: Dp,
    color: Color
) = Canvas(
    modifier = Modifier.size(size)
) {
    drawCircle(
        color = color,
        radius = this.size.width / 2,
        center = this.center
    )
}