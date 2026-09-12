package dev.sanmer.github.artifacts.ui.screen.workflow.component

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.AnimatedPoint
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.request.workflow.run.WorkflowRunStatus
import dev.sanmer.github.response.artifact.Artifact
import dev.sanmer.github.response.workflow.run.WorkflowRun

@Composable
fun WorkflowRunList(
    workflowRuns: LazyPagingItems<WorkflowRun>,
    artifacts: (WorkflowRun) -> LoadData<List<Artifact>>,
    onListArtifacts: (WorkflowRun) -> Unit,
    onDownloadArtifact: (Context, Artifact) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = modifier,
    state = state,
    contentPadding = contentPadding
) {
    items(
        count = workflowRuns.itemCount,
        key = workflowRuns.itemKey { it.id }
    ) { index ->
        workflowRuns[index]?.let {
            WorkflowRunItem(
                run = it,
                artifacts = artifacts(it),
                onListArtifacts = onListArtifacts,
                onDownloadArtifact = onDownloadArtifact
            )
        }
    }

    item {
        when (workflowRuns.loadState.append) {
            LoadState.Loading -> Loading(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            is LoadState.Error -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.link_break),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(45.dp)
                        .clickable(
                            onClick = { workflowRuns.retry() },
                            indication = ripple(bounded = false, radius = 45.dp),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun WorkflowRunItem(
    run: WorkflowRun,
    artifacts: LoadData<List<Artifact>>,
    onListArtifacts: (WorkflowRun) -> Unit,
    onDownloadArtifact: (Context, Artifact) -> Unit,
) {
    var expanded by rememberSaveable(run.id) { mutableStateOf(false) }
    val animatedDegrees by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    Row(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(
                enabled = run.status == WorkflowRunStatus.Completed,
                onClick = {
                    onListArtifacts(run)
                    expanded = !expanded
                }
            )
            .padding(all = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WorkflowRunStatusItem(
            status = run.conclusion ?: run.status,
            modifier = Modifier
                .padding(end = 15.dp)
                .align(Alignment.Top)
        )

        WorkflowRunItem(
            run = run,
            modifier = Modifier.weight(1f)
        )

        AnimatedContent(
            modifier = Modifier.padding(start = 5.dp),
            targetState = artifacts,
            transitionSpec = { fadeIn() + scaleIn() togetherWith scaleOut() + fadeOut() },
            contentAlignment = Alignment.Center
        ) {
            when (it) {
                LoadData.Loading -> CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )

                else -> Icon(
                    painter = painterResource(R.drawable.caret_right),
                    contentDescription = null,
                    modifier = Modifier.rotate(animatedDegrees)
                )
            }
        }
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        artifacts.onSuccess { list ->
            if (list.isNotEmpty()) ArtifactList(
                artifacts = list,
                onDownload = onDownloadArtifact
            )
        }
    }
}

@Composable
private fun WorkflowRunStatusItem(
    status: WorkflowRunStatus?,
    modifier: Modifier = Modifier
) = when (status) {
    WorkflowRunStatus.Success -> Icon(
        painter = painterResource(R.drawable.check_circle_fill),
        contentDescription = null,
        modifier = modifier,
        tint = when {
            isSystemInDarkTheme() -> Color(0xFF1B5E20)
            else -> Color(0xFF81C784)
        }
    )

    WorkflowRunStatus.Failure -> Icon(
        painter = painterResource(R.drawable.x_circle_fill),
        contentDescription = null,
        modifier = modifier,
        tint = when {
            isSystemInDarkTheme() -> Color(0xFFB71C1C)
            else -> Color(0xFFE57373)
        }
    )

    WorkflowRunStatus.Pending, WorkflowRunStatus.InProgress -> AnimatedPoint(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.size(24.dp)
    )

    WorkflowRunStatus.Cancelled, WorkflowRunStatus.Skipped -> Icon(
        painter = painterResource(R.drawable.prohibit_inset),
        contentDescription = null,
        modifier = modifier
    )

    WorkflowRunStatus.ActionRequired -> Icon(
        painter = painterResource(R.drawable.pause_circle),
        contentDescription = null,
        modifier = modifier
    )

    else -> Spacer(
        modifier = modifier.size(24.dp)
    )
}