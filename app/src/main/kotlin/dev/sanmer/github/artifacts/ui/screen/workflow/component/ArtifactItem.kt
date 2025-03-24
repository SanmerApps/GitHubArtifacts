package dev.sanmer.github.artifacts.ui.screen.workflow.component

import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.screen.home.component.Title
import dev.sanmer.github.artifacts.ui.screen.home.component.Value
import dev.sanmer.github.response.Artifact
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ArtifactItem(
    artifact: Artifact,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) = Row(
    modifier = Modifier
        .clickable(
            enabled = !artifact.expired,
            onClick = onClick
        )
        .padding(all = 15.dp)
        .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Title(
            title = artifact.name,
            subtitle = if (artifact.expired) {
                stringResource(id = R.string.artifact_expired)
            } else {
                null
            }
        )

        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.outline
        ) {
            BottomRow(artifact = artifact)
        }
    }

    if (!artifact.expired) trailing?.invoke()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BottomRow(
    artifact: Artifact
) = FlowRow(
    modifier = Modifier.padding(top = 5.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(5.dp)
) {
    val context = LocalContext.current
    val size by remember {
        derivedStateOf {
            Formatter.formatFileSize(context, artifact.sizeInBytes)
        }
    }
    val updatedAt by remember {
        derivedStateOf {
            artifact.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Value(
        icon = R.drawable.box,
        value = size
    )

    Value(
        value = updatedAt
    )
}
