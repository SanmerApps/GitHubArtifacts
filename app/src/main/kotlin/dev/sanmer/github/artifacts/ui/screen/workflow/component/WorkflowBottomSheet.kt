package dev.sanmer.github.artifacts.ui.screen.workflow.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.DragHandle
import dev.sanmer.github.artifacts.ui.component.Finished
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.artifacts.ui.ktx.bottom
import dev.sanmer.github.artifacts.ui.ktx.isEmpty
import dev.sanmer.github.response.workflow.Workflow

@Composable
fun WorkflowBottomSheet(
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