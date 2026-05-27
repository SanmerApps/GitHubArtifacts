package dev.sanmer.github.artifacts.ui.screen.workflow.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R

@Composable
fun QueryItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    isLoading: Boolean = false,
) = FilterChip(
    selected = selected,
    onClick = onClick,
    label = { Text(text = label) },
    leadingIcon = when {
        selected -> {
            {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        }

        isLoading -> {
            {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        }

        else -> null
    },
    trailingIcon = {
        Icon(
            painter = painterResource(R.drawable.caret_down),
            contentDescription = null,
            modifier = Modifier.size(FilterChipDefaults.IconSize * 0.65f)
        )
    }
)