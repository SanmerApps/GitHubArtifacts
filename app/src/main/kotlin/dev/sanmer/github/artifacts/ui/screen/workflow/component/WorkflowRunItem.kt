package dev.sanmer.github.artifacts.ui.screen.workflow.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.LabelText
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.response.workflow.run.WorkflowRun
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun WorkflowRunItem(
    run: WorkflowRun,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
) {
    val updatedAt by remember(run.id) {
        derivedStateOf {
            run.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Title(
        title = run.displayTitle,
        subtitle = run.headSha.take(7)
    )

    Values(
        run = run,
        modifier = Modifier.padding(vertical = 5.dp)
    )

    Value(
        value = updatedAt,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun Values(
    run: WorkflowRun,
    modifier: Modifier = Modifier
) = FlowRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
) {
    Value(
        value = run.name,
        color = MaterialTheme.colorScheme.outline
    )

    Value(
        icon = R.drawable.hash,
        value = run.runNumber,
        color = MaterialTheme.colorScheme.outline
    )

    Value(
        icon = R.drawable.user,
        value = run.actor.login,
        color = MaterialTheme.colorScheme.outline
    )

    LabelText(
        text = run.headBranch.short()
    )
}

private fun String.short(n: Int = 17) = substringBefore('/').let {
    if (it.length <= n) it else it.take(n) + "..."
}