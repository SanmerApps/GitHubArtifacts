package dev.sanmer.github.artifacts.ui.screen.token.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.database.entity.TokenWithRepo
import dev.sanmer.github.artifacts.ktx.toLocalDate
import dev.sanmer.github.artifacts.ui.screen.home.component.Title
import dev.sanmer.github.artifacts.ui.screen.home.component.Value
import kotlinx.datetime.TimeZone

@Composable
fun TokenItem(
    token: TokenWithRepo,
    onClick: () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .border(
            border = CardDefaults.outlinedCardBorder(),
            shape = MaterialTheme.shapes.medium
        )
        .clip(shape = MaterialTheme.shapes.medium)
        .clickable(
            onClick = onClick,
            enabled = true
        )
        .padding(all = 15.dp)
) {
    Title(
        title = token.token.name,
        subtitle = token.repo.size.toString()
    )

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.outline
    ) {
        BottomRow(token = token.token)
    }
}

@Composable
private fun BottomRow(
    token: TokenEntity
) = FlowRow(
    modifier = Modifier.padding(top = 5.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(5.dp)
) {
    val expiredAt by remember {
        derivedStateOf {
            token.expiredAt.toLocalDate(TimeZone.currentSystemDefault())
        }
    }

    Value(value = stringResource(id = R.string.token_expire, expiredAt))
}