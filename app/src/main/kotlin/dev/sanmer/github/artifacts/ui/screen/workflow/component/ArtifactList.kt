package dev.sanmer.github.artifacts.ui.screen.workflow.component

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.job.ArtifactJob
import dev.sanmer.github.artifacts.job.ArtifactJob.JobState
import dev.sanmer.github.artifacts.ui.ktx.surface
import dev.sanmer.github.response.artifact.Artifact

@Composable
fun ArtifactList(
    artifacts: List<Artifact>,
    onDownload: (Context, Artifact) -> Unit
) = Column(
    modifier = Modifier
        .padding(all = 10.dp)
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
) {
    val context = LocalContext.current

    artifacts.forEachIndexed { index, artifact ->
        ArtifactItem(
            artifact = artifact,
            onClick = { onDownload(context, artifact) }
        )

        if (index != artifacts.lastIndex) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(0.12f)
            )
        }
    }
}

@Composable
private fun ArtifactItem(
    artifact: Artifact,
    onClick: () -> Unit
) = Row(
    modifier = Modifier
        .clickable(
            onClick = onClick,
            enabled = !artifact.expired,
        )
        .padding(all = 15.dp)
        .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    val jobState by ArtifactJob.getJobState(artifact.id).collectAsStateWithLifecycle(JobState.Empty)

    ArtifactItem(
        artifact = artifact,
        modifier = Modifier.weight(1f)
    )

    AnimatedContent(
        targetState = jobState,
        transitionSpec = { (fadeIn() + scaleIn()) togetherWith (scaleOut() + fadeOut()) },
        contentAlignment = Alignment.Center,
        contentKey = { it.contentKey() }
    ) {
        when (it) {
            is JobState.Pending -> CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )

            is JobState.Running -> CircularProgressIndicator(
                strokeWidth = 2.dp,
                progress = { it.progress },
                modifier = Modifier.size(24.dp)
            )

            else -> Icon(
                painter = painterResource(R.drawable.download),
                contentDescription = null
            )
        }
    }
}

private fun JobState.contentKey() = when (this) {
    is JobState.Pending -> 1
    is JobState.Running -> 2
    else -> 3
}