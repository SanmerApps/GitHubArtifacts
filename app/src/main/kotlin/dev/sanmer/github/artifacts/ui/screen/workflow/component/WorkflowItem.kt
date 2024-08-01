package dev.sanmer.github.artifacts.ui.screen.workflow.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.screen.home.component.Title
import dev.sanmer.github.artifacts.ui.screen.home.component.Value
import dev.sanmer.github.response.WorkflowRun
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun WorkflowItem(
    run: WorkflowRun,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) = Row(
    modifier = Modifier
        .clip(shape = MaterialTheme.shapes.medium)
        .clickable(onClick = onClick)
        .padding(horizontal = 15.dp, vertical = 10.dp)
        .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Title(
            title = run.displayTitle,
            subtitle = run.headSha.substring(0, 7)
        )

        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.outline
        ) {
            BottomRow(run = run)
        }
    }

    trailing?.invoke()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BottomRow(
    run: WorkflowRun
) = FlowRow(
    modifier = Modifier.padding(top = 5.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(5.dp)
) {
    val updatedAt by remember {
        derivedStateOf {
            run.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Value(
        value = run.name
    )

    Value(
        icon = R.drawable.hash,
        value = run.runNumber
    )

    Value(
        icon = R.drawable.user,
        value = run.actor.login
    )

    Value(
        value = updatedAt
    )
}