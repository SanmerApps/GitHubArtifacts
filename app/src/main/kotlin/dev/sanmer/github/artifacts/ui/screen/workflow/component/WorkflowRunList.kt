package dev.sanmer.github.artifacts.ui.screen.workflow.component

import android.content.Context
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.AnimatedLinearWavy
import dev.sanmer.github.artifacts.ui.component.Dot
import dev.sanmer.github.artifacts.ui.component.StraightLine
import dev.sanmer.github.artifacts.ui.component.X
import dev.sanmer.github.artifacts.ui.ktx.items
import dev.sanmer.github.response.artifact.Artifact
import dev.sanmer.github.response.workflow.run.WorkflowRun

@Composable
fun WorkflowRunList(
    workflowRuns: LazyPagingItems<WorkflowRun>,
    artifacts: (WorkflowRun) -> LoadData<List<Artifact>>,
    onListArtifacts: (WorkflowRun) -> Unit,
    onDownloadArtifact: (Context, Artifact) -> Unit,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(),
    state = state,
    contentPadding = contentPadding
) {
    items(
        items = workflowRuns,
        key = { it.id }
    ) {
        WorkflowRunItem(
            run = it,
            artifacts = artifacts(it),
            onListArtifacts = onListArtifacts,
            onDownloadArtifact = onDownloadArtifact
        )
    }

    item {
        AppendIndicator(
            state = workflowRuns.loadState.append,
            onRetry = workflowRuns::retry
        )
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
            .clickable {
                onListArtifacts(run)
                expanded = !expanded
            }
            .padding(all = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WorkflowRunItem(
            run = run,
            modifier = Modifier.weight(1f)
        )

        AnimatedContent(
            targetState = artifacts,
            transitionSpec = { (fadeIn() + scaleIn()) togetherWith (scaleOut() + fadeOut()) },
            contentAlignment = Alignment.Center
        ) {
            when (it) {
                LoadData.Loading -> CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )

                else -> Icon(
                    painter = painterResource(R.drawable.chevron_right),
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
        when (artifacts) {
            is LoadData.Success<List<Artifact>> -> {
                if (artifacts.value.isNotEmpty()) {
                    ArtifactList(
                        artifacts = artifacts.value,
                        onDownload = onDownloadArtifact
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun AppendIndicator(
    state: LoadState,
    onRetry: () -> Unit
) = when (state) {
    LoadState.Loading -> AnimatedLinearWavy(
        modifier = Modifier
            .padding(all = 15.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
        strokeWidth = 2.dp
    )

    is LoadState.NotLoading -> End(
        color = MaterialTheme.colorScheme.outlineVariant
    ) {
        Dot(
            modifier = Modifier.size(6.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }

    is LoadState.Error -> End(
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        enabled = true,
        onClick = onRetry
    ) {
        X(
            modifier = Modifier.size(8.dp),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
            strokeWidth = 2.dp
        )
    }
}

@Composable
private fun End(
    color: Color,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) = Row(
    modifier = Modifier
        .clip(shape = CircleShape)
        .clickable(enabled = enabled, onClick = onClick)
        .padding(horizontal = 15.dp, vertical = 10.dp)
        .fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    StraightLine(
        modifier = Modifier.weight(1f),
        color = color,
        strokeWidth = 2.dp
    )

    content()

    StraightLine(
        modifier = Modifier.weight(1f),
        color = color,
        strokeWidth = 2.dp
    )
}