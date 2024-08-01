package dev.sanmer.github.artifacts.ui.screen.workflow.component

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.job.ArtifactJob
import dev.sanmer.github.artifacts.model.Data
import dev.sanmer.github.response.Artifact
import dev.sanmer.github.response.WorkflowRun

@Composable
fun WorkflowList(
    workflowRuns: List<WorkflowRun>,
    getArtifacts: (WorkflowRun) -> Data<List<Artifact>>,
    downloadArtifact: (Context, Artifact) -> Unit,
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
    items(workflowRuns) { run ->
        WorkflowItem(
            run = run,
            getArtifacts = getArtifacts,
            downloadArtifact = downloadArtifact
        )
    }
}

@Composable
private fun WorkflowItem(
    run: WorkflowRun,
    getArtifacts: (WorkflowRun) -> Data<List<Artifact>>,
    downloadArtifact: (Context, Artifact) -> Unit
) {
    var progress by remember { mutableStateOf(false) }
    var expend by rememberSaveable(run) { mutableStateOf(false) }
    val degrees by animateFloatAsState(
        targetValue = if (expend) 90f else 0f,
        label = "WorkflowItem Icon"
    )

    WorkflowItem(
        run = run,
        onClick = { expend = !expend },
        trailing = {
            WorkflowTrailing(
                progress = progress,
                degrees = degrees
            )
        }
    )

    if (expend) {
        val data = getArtifacts(run).apply { progress = !isCompleted }
        if (data is Data.Success && data.value.isNotEmpty()) {
            ArtifactList(
                artifacts = data.value,
                download = downloadArtifact
            )
        }
    }
}

@Composable
private fun WorkflowTrailing(
    progress: Boolean,
    degrees: Float
) = if (progress) {
    CircularProgressIndicator(
        strokeWidth = 2.dp,
        strokeCap = StrokeCap.Round,
        modifier = Modifier.size(24.dp)
    )
} else {
    Icon(
        painter = painterResource(id = R.drawable.chevron_right),
        contentDescription = null,
        modifier = Modifier.rotate(degrees)
    )
}

@Composable
private fun ArtifactList(
    artifacts: List<Artifact>,
    download: (Context, Artifact) -> Unit
) = Column(
    modifier = Modifier
        .padding(all = 10.dp)
        .clip(shape = MaterialTheme.shapes.medium)
        .border(
            border = CardDefaults.outlinedCardBorder(),
            shape = MaterialTheme.shapes.medium
        )
) {
    val context = LocalContext.current
    val size by remember { derivedStateOf { artifacts.size } }

    artifacts.forEachIndexed { index, artifact ->
        val jobState by ArtifactJob.getJobState(artifact.id).collectAsStateWithLifecycle(
            initialValue = ArtifactJob.JobState.Empty
        )

        ArtifactItem(
            artifact = artifact,
            onClick = { download(context, artifact) },
            trailing = {
                ArtifactTrailing(
                    jobState = jobState
                )
            }
        )

        if (index < size - 1) {
            HorizontalDivider()
        }
    }
}

@Composable
private fun ArtifactTrailing(
    jobState: ArtifactJob.JobState
) = when (jobState) {
    is ArtifactJob.JobState.Pending -> CircularProgressIndicator(
        strokeWidth = 2.dp,
        strokeCap = StrokeCap.Round,
        modifier = Modifier.size(24.dp)
    )

    is ArtifactJob.JobState.Running -> CircularProgressIndicator(
        strokeWidth = 2.dp,
        progress = { jobState.progress },
        strokeCap = StrokeCap.Round,
        modifier = Modifier.size(24.dp)
    )

    else -> Icon(
        painter = painterResource(id = R.drawable.download),
        contentDescription = null
    )
}