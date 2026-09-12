package dev.sanmer.github.artifacts.ui.screen.workflow.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.Const.DATETIME_DISPLAY
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.LabelText
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.request.workflow.run.WorkflowRunEvent
import dev.sanmer.github.request.workflow.run.WorkflowRunStatus
import dev.sanmer.github.response.workflow.run.WorkflowRun
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

@Composable
fun WorkflowRunItem(
    run: WorkflowRun,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(5.dp)
) {
    Title(title = run.displayTitle)

    Values(run = run)

    Timer(run = run)
}

@Composable
private fun Values(
    run: WorkflowRun,
    modifier: Modifier = Modifier
) = FlowRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
) {
    Value(
        value = run.name,
        color = MaterialTheme.colorScheme.outline
    )

    Value(
        icon = R.drawable.hash,
        value = run.runNumber.toString(),
        color = MaterialTheme.colorScheme.outline
    )

    Value(
        icon = R.drawable.user,
        value = run.actor.login,
        color = MaterialTheme.colorScheme.outline
    )

    if (run.event == WorkflowRunEvent.Push) LabelText(
        text = run.headBranch
    )
}

@Composable
private fun Timer(
    run: WorkflowRun,
    modifier: Modifier = Modifier
) = FlowRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
) {
    val createdAt by remember(run.id) {
        derivedStateOf {
            run.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
                .format(DATETIME_DISPLAY)
        }
    }
    val duration by remember(run.id) {
        derivedStateOf {
            (run.updatedAt - run.runStartedAt).toString()
        }
    }

    Value(
        value = createdAt,
        color = MaterialTheme.colorScheme.outline
    )

    if (run.status == WorkflowRunStatus.Completed) Value(
        icon = R.drawable.timer,
        value = duration,
        color = MaterialTheme.colorScheme.outline
    )
}