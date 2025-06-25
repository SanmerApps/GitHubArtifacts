package dev.sanmer.github.artifacts.ui.screen.workflow.component

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.job.ArtifactJob
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.ktx.items
import dev.sanmer.github.response.artifact.Artifact
import dev.sanmer.github.response.workflow.run.WorkflowRun

@Composable
fun WorkflowList(
    workflowRuns: LazyPagingItems<WorkflowRun>,
    getArtifacts: (WorkflowRun) -> LoadData<List<Artifact>>,
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
    items(
        items = workflowRuns,
        key = { it.id }
    ) { run ->
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
    getArtifacts: (WorkflowRun) -> LoadData<List<Artifact>>,
    downloadArtifact: (Context, Artifact) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var expend by rememberSaveable(run) { mutableStateOf(false) }

    WorkflowItem(
        run = run,
        onClick = { expend = !expend },
        trailing = {
            WorkflowTrailing(
                progress = isLoading,
                expend = expend
            )
        }
    )

    AnimatedVisibility(
        visible = expend,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val data by remember(run.id) {
            derivedStateOf {
                getArtifacts(run)
            }
        }

        DisposableEffect(data) {
            isLoading = data.isLoading
            onDispose { isLoading = false }
        }

        when (val data = data) {
            is LoadData.Success<List<Artifact>> -> {
                if (data.value.isNotEmpty()) {
                    ArtifactList(
                        artifacts = data.value,
                        download = downloadArtifact
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun WorkflowTrailing(
    progress: Boolean,
    expend: Boolean
) = Box(
    contentAlignment = Alignment.Center
) {
    val animateDegrees by animateFloatAsState(
        targetValue = if (expend && !progress) 90f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    AnimatedVisibility(
        visible = progress,
        enter = fadeIn() + scaleIn(),
        exit = scaleOut() + fadeOut()
    ) {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.size(24.dp)
        )
    }

    AnimatedVisibility(
        visible = !progress,
        enter = fadeIn() + scaleIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.chevron_right),
            contentDescription = null,
            modifier = Modifier
                .rotate(animateDegrees)
        )
    }
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

        if (index < artifacts.size - 1) {
            HorizontalDivider()
        }
    }
}

@Composable
private fun ArtifactTrailing(
    jobState: ArtifactJob.JobState
) = Box(
    contentAlignment = Alignment.Center
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (jobState.isStarting) 0.65f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    when (jobState) {
        is ArtifactJob.JobState.Pending -> CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.size(24.dp)
        )

        is ArtifactJob.JobState.Running -> CircularProgressIndicator(
            strokeWidth = 2.dp,
            progress = { jobState.progress },
            modifier = Modifier.size(24.dp)
        )

        else -> Unit
    }

    Icon(
        painter = painterResource(
            id = if (jobState.isStarting) R.drawable.file_type_zip else R.drawable.download
        ),
        contentDescription = null,
        modifier = Modifier.scale(animatedScale)
    )
}

private val ArtifactJob.JobState.isStarting
    inline get() = this is ArtifactJob.JobState.Pending || this is ArtifactJob.JobState.Running