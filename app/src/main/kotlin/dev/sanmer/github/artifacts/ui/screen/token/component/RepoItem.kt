package dev.sanmer.github.artifacts.ui.screen.token.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.Const.DATETIME_DISPLAY
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.artifacts.ui.screen.home.component.repoType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

@Composable
fun RepoItem(
    repo: Repo,
    onClick: () -> Unit
) = Column(
    modifier = Modifier
        .clip(MaterialTheme.shapes.medium)
        .clickable(onClick = onClick)
        .padding(horizontal = 15.dp, vertical = 10.dp)
        .fillMaxWidth()
) {
    val pushedAt by remember(repo.id) {
        derivedStateOf {
            repo.pushedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                .format(DATETIME_DISPLAY)
        }
    }

    Title(
        title = repo.fullName,
        subtitle = repo.repoType(),
        titleStyle = MaterialTheme.typography.bodyLarge
    )

    Value(
        value = pushedAt,
        color = MaterialTheme.colorScheme.outline
    )
}
