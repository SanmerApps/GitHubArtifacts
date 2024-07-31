package dev.sanmer.github.artifacts.ui.screen.workflow

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.model.Data
import dev.sanmer.github.artifacts.ui.component.Failed
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.artifacts.ui.component.NavigateUpTopBar
import dev.sanmer.github.artifacts.ui.screen.workflow.component.WorkflowList
import dev.sanmer.github.artifacts.viewmodel.WorkflowViewModel

@Composable
fun WorkflowScreen(
    viewModel: WorkflowViewModel = hiltViewModel(),
    navController: NavController
) {
    val workflowRuns by viewModel.workflowRuns.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                name = viewModel.name,
                onRefresh = viewModel::updateWorkflows,
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            targetState = workflowRuns,
            label = "WorkflowScreen"
        ) { data ->
            when (data) {
                Data.Loading, Data.None -> Loading(
                    modifier = Modifier.padding(contentPadding)
                )

                is Data.Failure -> Failed(
                    message = data.error.message,
                    modifier = Modifier.padding(contentPadding)
                )

                is Data.Success -> if (data.value.isEmpty()) {
                    Failed(
                        message = stringResource(id = R.string.no_workflow),
                        modifier = Modifier.padding(contentPadding)
                    )
                } else {
                    WorkflowList(
                        workflowRuns = data.value,
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
) = NavigateUpTopBar(
    title = name,
    actions = {
        IconButton(
            onClick = onRefresh
        ) {
            Icon(
                painter = painterResource(id = R.drawable.refresh),
                contentDescription = null
            )
        }
    },
    navController = navController,
    scrollBehavior = scrollBehavior
)