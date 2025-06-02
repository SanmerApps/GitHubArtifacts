package dev.sanmer.github.artifacts.ui.screen.home.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Title(
    title: String,
    subtitle: String?
) = FlowRow(
    horizontalArrangement = Arrangement.spacedBy(5.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp)
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.CenterVertically)
    )

    if (subtitle != null) {
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .border(
                    border = CardDefaults.outlinedCardBorder(),
                    shape = CircleShape
                )
                .padding(horizontal = 10.dp)
        )
    }
}