package dev.sanmer.github.artifacts.ui.screen.setting.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.ui.component.Logo

@Composable
fun SettingItem(
    icon: @Composable () -> Unit,
    title: String,
    text: String,
    onClick: () -> Unit
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .clip(shape = MaterialTheme.shapes.large)
        .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
        .border(
            border = CardDefaults.outlinedCardBorder(),
            shape = MaterialTheme.shapes.large
        )
        .clickable(onClick = onClick)
        .padding(all = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(20.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    icon()

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun SettingIcon(
    @DrawableRes icon: Int,
    color: Color
) = Logo(
    icon = icon,
    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor = color,
    modifier = Modifier.size(40.dp)
)