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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import dev.sanmer.github.artifacts.ui.component.DragHandle
import dev.sanmer.github.artifacts.ui.component.Finished
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.artifacts.ui.component.appBarContainerColor
import dev.sanmer.github.artifacts.ui.ktx.bottom
import dev.sanmer.github.artifacts.ui.ktx.horizontal
import dev.sanmer.github.artifacts.ui.ktx.isEmpty
import dev.sanmer.github.artifacts.ui.ktx.isLoading
import dev.sanmer.github.artifacts.ui.ktx.isNotEmpty
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.ktx.vertical
import dev.sanmer.github.artifacts.ui.screen.workflow.component.FilterItem
import dev.sanmer.github.artifacts.ui.screen.workflow.component.QueryItem
import dev.sanmer.github.artifacts.ui.screen.workflow.component.WorkflowList
import dev.sanmer.github.artifacts.ui.screen.workflow.component.WorkflowRunList
import dev.sanmer.github.response.artifact.Artifact
import dev.sanmer.github.response.workflow.Workflow
import dev.sanmer.github.response.workflow.run.WorkflowRun
import kotlinx.coroutines.launch

@Composable
fun WorkflowScreen(
    viewModel: WorkflowViewModel,
    goBack: () -> Unit
) {
    val workflows = viewModel.workflows.collectAsLazyPagingItems()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val workflowRuns = viewModel.workflowRuns.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                name = viewModel.name,
                onBack = goBack,
                isRefreshing = workflowRuns.isNotEmpty() && workflowRuns.loadState.refresh.isLoading,
                onRefresh = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                        workflowRuns.refresh()
                    }
                },
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
                onUpdateQuery = viewModel::updateQuery,
                scrollBehavior = scrollBehavior,
                contentPadding = contentPadding.horizontal()
            )

            WorkflowRunContent(
                workflowRuns = workflowRuns,
                artifacts = viewModel::artifacts,
                onListArtifacts = viewModel::listArtifacts,
                onDownloadArtifact = viewModel::downloadArtifact,
                modifier = Modifier.padding(contentPadding.horizontal()),
                listState = listState
            )
        }
    }
}

@Composable
private fun QueryBar(
    workflows: LazyPagingItems<Workflow>,
    query: WorkflowViewModel.RunsQuery,
    onUpdateQuery: ((WorkflowViewModel.RunsQuery) -> WorkflowViewModel.RunsQuery) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    contentPadding: PaddingValues = PaddingValues(all = 0.dp)
) {
    val (bottomSheet, setBottomSheet) = remember { mutableStateOf(BottomSheet.None) }
    when (bottomSheet) {
        BottomSheet.None -> {}
        BottomSheet.Workflow -> WorkflowBottomSheet(
            onClose = { setBottomSheet(BottomSheet.None) },
            workflows = workflows,
            workflow = query.workflow,
            onWorkflowChange = { workflow -> onUpdateQuery { it.copy(workflow = workflow) } }
        )

        BottomSheet.Event -> EnumValuesBottomSheet(
            onClose = { setBottomSheet(BottomSheet.None) },
            title = stringResource(R.string.workflow_run_event),
            value = query.event,
            onValueChange = { event -> onUpdateQuery { it.copy(event = event) } }
        )

        BottomSheet.Status -> EnumValuesBottomSheet(
            onClose = { setBottomSheet(BottomSheet.None) },
            title = stringResource(R.string.workflow_run_status),
            value = query.status,
            onValueChange = { status -> onUpdateQuery { it.copy(status = status) } }
        )
    }

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
                    setBottomSheet(BottomSheet.Workflow)
                }
            },
            label = query.workflow?.name ?: stringResource(R.string.workflow_name),
            isLoading = workflows.loadState.isLoading
        )

        QueryItem(
            selected = query.event != null,
            onClick = { setBottomSheet(BottomSheet.Event) },
            label = query.event?.name ?: stringResource(R.string.workflow_run_event)
        )

        QueryItem(
            selected = query.status != null,
            onClick = { setBottomSheet(BottomSheet.Status) },
            label = query.status?.name ?: stringResource(R.string.workflow_run_status)
        )
    }
}

enum class BottomSheet {
    None,
    Workflow,
    Event,
    Status
}

@Composable
private fun WorkflowBottomSheet(
    onClose: () -> Unit,
    workflows: LazyPagingItems<Workflow>,
    workflow: Workflow?,
    onWorkflowChange: (Workflow?) -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = MaterialTheme.shapes.large.bottom(0.dp),
    dragHandle = null
) {
    DragHandle()

    Text(
        text = stringResource(R.string.workflow_name),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Crossfade(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 10.dp),
        targetState = workflows.loadState.refresh
    ) {
        when (it) {
            LoadState.Loading if (workflows.isEmpty()) -> Loading(
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
            )

            is LoadState.Error -> Finished(
                label = it.error.message ?: it.error.javaClass.name,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .height(240.dp)
                    .fillMaxWidth()
            )

            else -> if (workflows.isEmpty()) {
                Finished(
                    label = R.string.workflow_empty,
                    modifier = Modifier.height(240.dp)
                )
            } else {
                WorkflowList(
                    workflows = workflows,
                    workflow = workflow,
                    onWorkflowChange = onWorkflowChange
                )
            }
        }
    }
}

@Composable
private inline fun <reified T : Enum<T>> EnumValuesBottomSheet(
    noinline onClose: () -> Unit,
    title: String,
    value: T?,
    crossinline onValueChange: (T?) -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = MaterialTheme.shapes.large.bottom(0.dp),
    dragHandle = null
) {
    DragHandle()

    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Spacer(modifier = Modifier.height(10.dp))

    FlowRow(
        modifier = Modifier.padding(all = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        enumValues<T>().forEach {
            FilterItem(
                selected = it == value,
                onClick = { onValueChange(if (it == value) null else it) },
                label = it.name
            )
        }
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

        else -> if (workflowRuns.isEmpty()) {
            Finished(
                label = R.string.workflow_run_empty,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            WorkflowRunList(
                workflowRuns = workflowRuns,
                artifacts = artifacts,
                onListArtifacts = onListArtifacts,
                onDownloadArtifact = onDownloadArtifact,
                state = listState
            )
        }
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
                        targetValue = rotation.value - 360f,
                        animationSpec = tween(
                            durationMillis = 1200,
                            easing = LinearEasing
                        )
                    )
                }
            }

            Icon(
                painter = painterResource(R.drawable.refresh),
                contentDescription = null,
                modifier = Modifier.rotate(rotation.value)
            )
        }
    },
    scrollBehavior = scrollBehavior
)