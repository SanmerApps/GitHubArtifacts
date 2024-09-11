package dev.sanmer.github.artifacts.ui.screen.workflow

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.Failed
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.artifacts.ui.component.PageIndicator
import dev.sanmer.github.artifacts.ui.ktx.isEmpty
import dev.sanmer.github.artifacts.ui.screen.workflow.component.WorkflowList
import dev.sanmer.github.artifacts.viewmodel.WorkflowViewModel

@Composable
fun WorkflowScreen(
    viewModel: WorkflowViewModel = hiltViewModel(),
    navController: NavController
) {
    if (viewModel.name.isEmpty()) return
    val workflowRuns = viewModel.workflowRuns.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                name = viewModel.name,
                onRefresh = workflowRuns::refresh,
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            targetState = workflowRuns.loadState.refresh,
            label = "WorkflowScreen"
        ) { state ->
            when (state) {
                is LoadState.Error -> Failed(
                    message = state.error.message,
                    modifier = Modifier.padding(contentPadding)
                )

                is LoadState.Loading -> Loading(
                    modifier = Modifier.padding(contentPadding)
                )

                else -> if (workflowRuns.isEmpty()) {
                    PageIndicator(
                        icon = R.drawable.cloud_computing,
                        text = stringResource(id = R.string.workflow_empty),
                        modifier = Modifier.padding(contentPadding)
                    )
                } else {
                    WorkflowList(
                        workflowRuns = workflowRuns,
                        getArtifacts = viewModel::getArtifacts,
                        downloadArtifact = viewModel::downloadArtifact,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    name: String,
    onRefresh: () -> Unit,
    navController: NavController,
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
            onClick = { navController.navigateUp() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = {
        IconButton(
            onClick = onRefresh,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.refresh),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)