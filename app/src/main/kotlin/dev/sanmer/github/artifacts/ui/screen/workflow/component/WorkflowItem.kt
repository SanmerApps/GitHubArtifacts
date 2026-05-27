package dev.sanmer.github.artifacts.ui.screen.workflow.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.response.workflow.Workflow
import dev.sanmer.github.response.workflow.WorkflowState

@Composable
fun WorkflowItem(
    workflow: Workflow,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
) {
    Title(
        title = workflow.name,
        subtitle = when (workflow.state) {
            WorkflowState.Active -> null
            WorkflowState.Deleted -> stringResource(R.string.workflow_deleted)
            WorkflowState.DisabledFork,
            WorkflowState.DisabledInactivity,
            WorkflowState.DisabledManually -> stringResource(R.string.workflow_disabled)
        }
    )

    Value(
        value = workflow.path,
        color = MaterialTheme.colorScheme.outline
    )
}