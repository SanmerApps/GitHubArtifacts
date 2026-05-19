package dev.sanmer.github.artifacts.ui.screen.token.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.artifacts.ui.screen.home.component.repoType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun RepoItem(
    repo: RepoEntity,
    onDelete: () -> Unit,
    onWorkflow: () -> Unit,
) = Row(
    modifier = Modifier
        .padding(horizontal = 15.dp, vertical = (2.5).dp)
        .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
) {
    val updatedAt by remember(repo.id) {
        derivedStateOf {
            repo.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Title(
            title = repo.fullName,
            subtitle = repo.repoType(),
            titleStyle = MaterialTheme.typography.bodyLarge,
            subtitleStyle = MaterialTheme.typography.labelMedium
        )

        Value(
            value = updatedAt,
            color = MaterialTheme.colorScheme.outline
        )
    }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        var expanded by remember { mutableStateOf(false) }

        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.dots),
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.medium,
            border = CardDefaults.outlinedCardBorder(false),
            shadowElevation = 0.dp
        ) {
            MenuItem(
                text = R.string.workflow_title,
                icon = R.drawable.subtask,
                onClick = {
                    expanded = false
                    onWorkflow()
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            MenuItem(
                text = R.string.edit_delete,
                icon = R.drawable.trash_x,
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
private fun MenuItem(
    @StringRes text: Int,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) = DropdownMenuItem(
    text = { Text(text = stringResource(id = text)) },
    onClick = onClick,
    leadingIcon = {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null
        )
    },
    modifier = Modifier
        .padding(horizontal = 10.dp)
        .clip(MaterialTheme.shapes.small),
    contentPadding = PaddingValues(all = 10.dp)
)