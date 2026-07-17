package dev.sanmer.github.artifacts.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun Value(
    value: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current
) = Text(
    text = value,
    modifier = modifier,
    color = color,
    style = MaterialTheme.typography.bodyMedium,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)

@Composable
fun Value(
    icon: @Composable () -> Unit,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(5.dp)
) {
    icon()
    Value(
        value = value,
        color = color
    )
}

@Composable
fun Value(
    @DrawableRes icon: Int,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current
) = Value(
    icon = {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = color
        )
    },
    value = value,
    modifier = modifier,
    color = color
)