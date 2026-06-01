package dev.sanmer.github.artifacts.ui.screen.workflow.component

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.ktx.items
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.ktx.surface
import dev.sanmer.github.response.workflow.Workflow

@Composable
fun WorkflowList(
    workflows: LazyPagingItems<Workflow>,
    workflow: Workflow?,
    onWorkflowChange: (Workflow?) -> Unit,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(),
    state = state,
    contentPadding = contentPadding + PaddingValues(all = 15.dp),
    verticalArrangement = Arrangement.spacedBy(15.dp)
) {
    items(
        items = workflows,
        key = { it.id }
    ) {
        WorkflowItem(
            workflow = it,
            selected = it == workflow,
            onClick = { onWorkflowChange(if (it == workflow) null else it) }
        )
    }
}

@Composable
private fun WorkflowItem(
    workflow: Workflow,
    selected: Boolean,
    onClick: () -> Unit
) = Row(
    modifier = Modifier
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .clickable(onClick = onClick)
        .padding(all = 15.dp)
        .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(15.dp)
) {
    WorkflowItem(
        workflow = workflow,
        modifier = Modifier.weight(1f)
    )

    if (selected) {
        Icon(
            painter = painterResource(R.drawable.circle_check_filled),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp)
        )
    }
}