package dev.sanmer.github.artifacts.ui.screen.home.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun Value(
    value: Any,
    modifier: Modifier = Modifier
) = Text(
    text = value.toString(),
    style = MaterialTheme.typography.bodyMedium,
    modifier = modifier
)

@Composable
fun RowScope.Value(
    value: Any
) = Value(
    value = value,
    modifier = Modifier.align(Alignment.CenterVertically)
)

@Composable
fun RowScope.Value(
    icon: @Composable () -> Unit,
    value: Any
) = Row(
    modifier = Modifier.align(Alignment.CenterVertically),
    verticalAlignment = Alignment.CenterVertically
) {
    icon()
    Spacer(modifier = Modifier.width(2.dp))
    Value(value = value)
}

@Composable
fun RowScope.Value(
    @DrawableRes icon: Int,
    value: Any
) = Value(
    icon = {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    },
    value = value
)