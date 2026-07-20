package dev.sanmer.github.artifacts.ui.screen.token.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.database.model.Token
import dev.sanmer.github.artifacts.ktx.toLocalDate
import dev.sanmer.github.artifacts.ui.component.LabelText
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.artifacts.ui.ktx.surface
import kotlinx.datetime.TimeZone

@Composable
fun TokenItem(
    token: Token,
    repos: List<Repo>,
    onClick: () -> Unit
) = Column(
    modifier = Modifier
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .clickable(onClick = onClick)
        .padding(all = 15.dp)
        .fillMaxWidth(),
) {
    val expiredAt by remember(token.id) {
        derivedStateOf {
            token.expiredAt.toLocalDate(TimeZone.currentSystemDefault())
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Title(title = token.name)

            Value(
                value = stringResource(R.string.token_expire, expiredAt),
                color = MaterialTheme.colorScheme.outline
            )
        }

        Text(
            text = repos.size.toString(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (repos.isNotEmpty()) {
        Values(
            repos = repos,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun Values(
    repos: List<Repo>,
    modifier: Modifier = Modifier
) = FlowRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    maxLines = 2
) {
    repos.forEach {
        LabelText(text = it.name)
    }
}