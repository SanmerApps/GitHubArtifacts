package dev.sanmer.github.artifacts.ui.screen.workflow

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.Finished
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.artifacts.ui.component.appBarContainerColor
import dev.sanmer.github.artifacts.ui.ktx.horizontal
import dev.sanmer.github.artifacts.ui.ktx.isEmpty
import dev.sanmer.github.artifacts.ui.ktx.isLoading
import dev.sanmer.github.artifacts.ui.ktx.isNotEmpty
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.ktx.vertical
import dev.sanmer.github.artifacts.ui.screen.workflow.WorkflowViewModel.BottomSheet
import dev.sanmer.github.artifacts.ui.screen.workflow.component.EnumBottomSheet
import dev.sanmer.github.artifacts.ui.screen.workflow.component.QueryItem
import dev.sanmer.github.artifacts.ui.screen.workflow.component.WorkflowBottomSheet
import dev.sanmer.github.artifacts.ui.screen.workflow.component.WorkflowRunList
import dev.sanmer.github.response.artifact.Artifact
import dev.sanmer.github.response.workflow.Workflow
import dev.sanmer.github.response.workflow.run.WorkflowRun

@Composable
fun WorkflowScreen(
    viewModel: WorkflowViewModel,
    goBack: () -> Unit
) {
    val workflows = viewModel.workflows.collectAsLazyPagingItems()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val workflowRuns = viewModel.workflowRuns.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    when (viewModel.bottomSheet) {
        BottomSheet.None -> {}
        BottomSheet.Workflow -> WorkflowBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            workflows = workflows,
            workflow = query.workflow,
            onWorkflowChange = { workflow -> viewModel.updateQuery { it.copy(workflow = workflow) } }
        )

        BottomSheet.Event -> EnumBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            title = stringResource(R.string.workflow_run_event),
            value = query.event,
            onValueChange = { event -> viewModel.updateQuery { it.copy(event = event) } }
        )

        BottomSheet.Status -> EnumBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            title = stringResource(R.string.workflow_run_status),
            value = query.status,
            onValueChange = { status -> viewModel.updateQuery { it.copy(status = status) } }
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                name = viewModel.name,
                onBack = goBack,
                isRefreshing = workflowRuns.isNotEmpty() && workflowRuns.loadState.refresh.isLoading,
                onRefresh = workflowRuns::refresh,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding.vertical())
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
        ) {
            QueryBar(
                workflows = workflows,
                query = query,
                onBottomSheet = { viewModel.bottomSheet = it },
                scrollBehavior = scrollBehavior,
                contentPadding = contentPadding.horizontal()
            )

            WorkflowRunContent(
                workflowRuns = workflowRuns,
                artifacts = viewModel::artifacts,
                onListArtifacts = viewModel::listArtifacts,
                onDownloadArtifact = viewModel::downloadArtifact,
                modifier = Modifier.padding(contentPadding.horizontal())
            )
        }
    }
}

@Composable
private fun QueryBar(
    workflows: LazyPagingItems<Workflow>,
    query: WorkflowViewModel.RunsQuery,
    onBottomSheet: (BottomSheet) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    contentPadding: PaddingValues = PaddingValues(all = 0.dp)
) {
    val containerColor by appBarContainerColor(scrollBehavior)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
            .horizontalScroll(rememberScrollState())
            .padding(contentPadding + PaddingValues(all = 15.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QueryItem(
            selected = query.workflow != null,
            onClick = {
                if (workflows.loadState.hasError) {
                    workflows.retry()
                } else {
                    onBottomSheet(BottomSheet.Workflow)
                }
            },
            label = query.workflow?.name ?: stringResource(R.string.workflow_name),
            isLoading = workflows.loadState.isLoading
        )

        QueryItem(
            selected = query.event != null,
            onClick = { onBottomSheet(BottomSheet.Event) },
            label = query.event?.name ?: stringResource(R.string.workflow_run_event)
        )

        QueryItem(
            selected = query.status != null,
            onClick = { onBottomSheet(BottomSheet.Status) },
            label = query.status?.name ?: stringResource(R.string.workflow_run_status)
        )
    }
}

@Composable
private fun WorkflowRunContent(
    workflowRuns: LazyPagingItems<WorkflowRun>,
    artifacts: (WorkflowRun) -> LoadData<List<Artifact>>,
    onListArtifacts: (WorkflowRun) -> Unit,
    onDownloadArtifact: (Context, Artifact) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) = Crossfade(
    modifier = modifier.fillMaxSize(),
    targetState = workflowRuns.loadState.refresh
) {
    when (it) {
        LoadState.Loading if (workflowRuns.isEmpty()) -> Loading(
            modifier = Modifier.fillMaxSize()
        )

        is LoadState.Error -> Finished(
            label = it.error.message ?: it.error.javaClass.name,
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxSize(),
        )

        else -> WorkflowRunList(
            workflowRuns = workflowRuns,
            artifacts = artifacts,
            onListArtifacts = onListArtifacts,
            onDownloadArtifact = onDownloadArtifact,
            state = listState
        )
    }
}

@Composable
private fun TopBar(
    name: String,
    onBack: () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(
            text = name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    },
    navigationIcon = {
        IconButton(
            onClick = onBack,
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = {
        IconButton(
            onClick = onRefresh,
        ) {
            val rotation = remember { Animatable(0f) }
            LaunchedEffect(isRefreshing) {
                if (isRefreshing) {
                    rotation.animateTo(
                        targetValue = rotation.value + 360f,
                        animationSpec = tween(
                            durationMillis = 1200,
                            easing = LinearEasing
                        )
                    )
                }
            }

            Icon(
                painter = painterResource(R.drawable.arrows_clockwise),
                contentDescription = null,
                modifier = Modifier.rotate(rotation.value)
            )
        }
    },
    scrollBehavior = scrollBehavior
)