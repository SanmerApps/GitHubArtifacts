package dev.sanmer.github.artifacts.ui.screen.workflow.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.Dot
import dev.sanmer.github.artifacts.ui.component.DragHandle
import dev.sanmer.github.artifacts.ui.ktx.bottom
import kotlin.enums.enumEntries

@Composable
inline fun <reified T : Enum<T>> EnumBottomSheet(
    crossinline onClose: () -> Unit,
    title: String,
    value: T?,
    crossinline onValueChange: (T?) -> Unit
) = ModalBottomSheet(
    onDismissRequest = { onClose() },
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
        enumEntries<T>().forEach {
            FilterItem(
                selected = it == value,
                onClick = { onValueChange(if (it == value) null else it) },
                label = it.name
            )
        }
    }
}

@Composable
fun FilterItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
) = FilterChip(
    selected = selected,
    onClick = onClick,
    label = { Text(text = label) },
    shape = CircleShape,
    leadingIcon = when {
        selected -> null
        else -> {
            {
                Dot(
                    modifier = Modifier.size(8.dp),
                    color = LocalContentColor.current
                )
            }
        }
    },
    trailingIcon = when {
        selected -> {
            {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null
                )
            }
        }

        else -> null
    }
)