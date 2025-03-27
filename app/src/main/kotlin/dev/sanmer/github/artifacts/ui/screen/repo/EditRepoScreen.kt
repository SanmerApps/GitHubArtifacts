package dev.sanmer.github.artifacts.ui.screen.repo

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.Failed
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.viewmodel.EditRepoViewModel
import dev.sanmer.github.artifacts.viewmodel.EditRepoViewModel.Value

@Composable
fun EditRepoScreen(
    viewModel: EditRepoViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(
        enabled = viewModel.data is LoadData.Failure,
        onBack = viewModel::rewind
    )

    Scaffold(
        topBar = {
            TopBar(
                edit = viewModel.edit,
                onSave = {
                    viewModel.save { if (!viewModel.edit) navController.navigateUp() }
                },
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            targetState = viewModel.data
        ) { data ->
            when (data) {
                LoadData.Loading -> Loading(
                    modifier = Modifier.padding(contentPadding)
                )

                is LoadData.Failure -> Failed(
                    message = data.error.message,
                    modifier = Modifier.padding(contentPadding)
                )

                is LoadData.Success, LoadData.None -> EditContent(
                    contentPadding = contentPadding
                )
            }
        }
    }
}

@Composable
private fun EditContent(
    viewModel: EditRepoViewModel = hiltViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    contentPadding = contentPadding + PaddingValues(vertical = 15.dp, horizontal = 5.dp),
    verticalArrangement = Arrangement.spacedBy(15.dp)
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon = R.drawable.user)

            OutlinedTextField(
                value = viewModel.input.owner,
                onValueChange = { owner ->
                    viewModel.updateInput { it.copy(owner = owner) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = MaterialTheme.shapes.medium,
                label = { Text(text = stringResource(id = R.string.edit_owner)) },
                readOnly = viewModel.edit,
                isError = viewModel.isError(Value.Owner),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.size(48.dp))
        }
    }

    item {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(48.dp))

            OutlinedTextField(
                value = viewModel.input.name,
                onValueChange = { name ->
                    viewModel.updateInput { it.copy(name = name) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                shape = MaterialTheme.shapes.medium,
                label = { Text(text = stringResource(id = R.string.edit_name)) },
                readOnly = viewModel.edit,
                isError = viewModel.isError(Value.Name),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.size(48.dp))
        }
    }

    itemsIndexed(viewModel.tokens) { index, token ->
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (index == 0) {
                Icon(icon = R.drawable.key)
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            TokenItem(
                token = token,
                selected = viewModel.input.token == token.token,
                onClick = { viewModel.updateInput { it.copy(token = token.token) } },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
private fun TokenItem(
    token: TokenEntity,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .clip(shape = MaterialTheme.shapes.medium)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.medium
        )
        .clickable(onClick = onClick)
        .padding(all = 15.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Text(
        text = token.name,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.weight(1f)
    )

    if (selected) {
        Icon(
            painter = painterResource(id = R.drawable.circle_check),
            contentDescription = null
        )
    }
}

@Composable
private fun TopBar(
    edit: Boolean,
    onSave: () -> Unit,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(
            text = stringResource(
                id = if (edit) R.string.edit_repo_title else R.string.add_repo_title
            )
        )
    },
    navigationIcon = {
        IconButton(
            onClick = { navController.navigateUp() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.x),
                contentDescription = null
            )
        }
    },
    actions = {
        IconButton(
            onClick = onSave
        ) {
            Icon(
                painter = painterResource(id = R.drawable.device_floppy),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun Icon(
    @DrawableRes icon: Int
) = Box(
    modifier = Modifier.size(48.dp),
    contentAlignment = Alignment.Center
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = null
    )
}