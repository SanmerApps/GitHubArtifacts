package dev.sanmer.github.artifacts.ui.screen.workflow.component

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.Const.DATETIME_DISPLAY
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.response.artifact.Artifact
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

@Composable
fun ArtifactItem(
    artifact: Artifact,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(5.dp)
) {
    Title(
        title = artifact.name,
        subtitle = if (artifact.expired) {
            stringResource(R.string.artifact_expired)
        } else {
            null
        }
    )

    Values(artifact = artifact)
}

@Composable
private fun Values(
    artifact: Artifact
) = FlowRow(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
) {
    val context = LocalContext.current
    val size by remember(artifact.id) {
        derivedStateOf {
            Formatter.formatFileSize(context, artifact.sizeInBytes)
        }
    }
    val updatedAt by remember(artifact.id) {
        derivedStateOf {
            artifact.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                .format(DATETIME_DISPLAY)
        }
    }

    Value(
        icon = R.drawable.box,
        value = size,
        color = MaterialTheme.colorScheme.outline
    )

    Value(
        value = updatedAt,
        color = MaterialTheme.colorScheme.outline
    )
}
