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
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.artifacts.ui.ktx.surface
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
    val pushedAt by remember(repo.id) {
        derivedStateOf {
            repo.pushedAt.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Title(
            title = repo.fullName,
            subtitle = repo.repoType(),
            titleStyle = MaterialTheme.typography.bodyLarge
        )

        Value(
            value = pushedAt,
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
                painter = painterResource(R.drawable.dots),
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
                onClick = {
                    expanded = false
                    onWorkflow()
                },
                text = R.string.workflow_title,
                icon = R.drawable.subtask
            )

            Spacer(modifier = Modifier.height(8.dp))

            MenuItem(
                onClick = {
                    expanded = false
                    onDelete()
                },
                text = R.string.edit_delete,
                icon = R.drawable.trash_x,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        }
    }
}

@Composable
private fun MenuItem(
    onClick: () -> Unit,
    @StringRes text: Int,
    @DrawableRes icon: Int,
    contentColor: Color = Color.Unspecified,
    containerColor: Color = Color.Unspecified
) = DropdownMenuItem(
    text = { Text(text = stringResource(text)) },
    onClick = onClick,
    leadingIcon = {
        Icon(
            painter = painterResource(icon),
            contentDescription = null
        )
    },
    modifier = Modifier
        .padding(horizontal = 8.dp)
        .surface(
            shape = MaterialTheme.shapes.small,
            backgroundColor = containerColor
        ),
    contentPadding = PaddingValues(all = 10.dp),
    colors = MenuDefaults.itemColors(
        textColor = contentColor,
        leadingIconColor = contentColor,
        trailingIconColor = contentColor
    )
)