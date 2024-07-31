package dev.sanmer.github.artifacts.ui.screen.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sanmer.github.Languages
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ktx.format
import dev.sanmer.github.response.Repository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun RepoItem(
    repo: Repository,
    onClick: () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(all = 15.dp)
) {
    Title(
        title = repo.fullName,
        subtitle = repo.state()
    )

    if (repo.description.isNotBlank()) {
        Text(
            text = repo.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.outline
    ) {
        BottomRow(repo = repo)
    }
}

@Composable
private fun Repository.state(): String {
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
    repo: Repository
) = FlowRow(
    modifier = Modifier.padding(top = 5.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(5.dp)
) {
    val pushedAt by remember {
        derivedStateOf {
            repo.pushedAt.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    if (repo.language.isNotBlank()) {
        Value(
            icon = {
                Point(
                    size = 10.dp,
                    color = Color(Languages.color(repo.language))
                )
            },
            value = repo.language
        )
    }

    if (!repo.license.isEmpty) {
        Value(
            icon = R.drawable.scale,
            value = repo.license.name
        )
    }

    Value(
        icon = R.drawable.git_fork,
        value = repo.forksCount.format()
    )

    Value(
        icon = R.drawable.star,
        value = repo.stargazersCount.format()
    )

    if (repo.hasIssues) {
        Value(
            icon = R.drawable.circle_dot,
            value = repo.openIssuesCount.format()
        )
    }

    Value(
        value = pushedAt
    )
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