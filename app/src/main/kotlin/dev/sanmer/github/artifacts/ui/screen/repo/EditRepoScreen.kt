package dev.sanmer.github.artifacts.ui.screen.repo

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.ktx.toLocalDate
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.BasicDialog
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.ktx.surface
import kotlinx.datetime.TimeZone
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditRepoScreen(
    viewModel: EditRepoViewModel = koinViewModel(),
    goBack: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(true) {
        onDispose { keyboardController?.hide() }
    }

    when (val data = viewModel.loadData) {
        LoadData.Pending -> {}
        LoadData.Loading -> Loading(
            onClose = viewModel::revertLoadData
        )

        is LoadData.Success<*> -> {
            if (viewModel.isEdit) viewModel.revertLoadData()
        }

        is LoadData.Failure -> Failure(
            onClose = viewModel::revertLoadData,
            error = data.error
        )
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                onClose = goBack,
                onDelete = { viewModel.delete(goBack) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                onClick = { viewModel.save { if (!viewModel.isEdit) goBack() } }
            )
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding + PaddingValues(all = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            item {
                RepoItem(
                    owner = viewModel.input.owner,
                    name = viewModel.input.name,
                    readOnly = viewModel.isEdit
                )
            }

            items(viewModel.tokens) { token ->
                TokenItem(
                    token = token,
                    selected = viewModel.input.tokenIdValue == token.id,
                    onClick = { viewModel.input.tokenIdValue = token.id }
                )
            }
        }
    }
}

@Composable
private fun RepoItem(
    owner: TextFieldState,
    name: TextFieldState,
    readOnly: Boolean = false
) = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
) {
    OutlinedTextField(
        state = owner,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        placeholder = { Text(text = stringResource(id = R.string.edit_owner)) },
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )

    Icon(
        painter = painterResource(R.drawable.slash),
        contentDescription = null
    )

    OutlinedTextField(
        state = name,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        placeholder = { Text(text = stringResource(id = R.string.edit_name)) },
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun TokenItem(
    token: TokenEntity,
    selected: Boolean,
    onClick: () -> Unit
) = Row(
    modifier = Modifier
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .clickable(onClick = onClick)
        .padding(all = 15.dp)
        .fillMaxWidth()
) {
    val expiredAt by remember(token.id) {
        derivedStateOf {
            token.expiredAt.toLocalDate(TimeZone.currentSystemDefault())
        }
    }

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Title(title = token.name)

        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.outline
        ) {
            Value(text = stringResource(id = R.string.token_expire, expiredAt))
        }
    }

    if (selected) {
        Spacer(Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.circle_check_filled),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.Top)
        )
    }
}

@Composable
private fun TopBar(
    isEdit: Boolean,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val isImeVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    if (isEdit) R.string.edit_repo_title
                    else R.string.add_repo_title
                )
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (isImeVisible) keyboardController?.hide()
                    onClose()
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.x),
                    contentDescription = null
                )
            }
        },
        actions = {
            if (isEdit) {
                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.trash_x),
                        contentDescription = null
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ActionButton(
    onClick: () -> Unit
) {
    val isImeVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    FloatingActionButton(
        onClick = {
            if (isImeVisible) keyboardController?.hide()
            onClick()
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.device_floppy),
            contentDescription = null
        )
    }
}

@Composable
private fun Loading(
    onClose: () -> Unit
) = BasicDialog(
    onDismissRequest = onClose,
) {
    CircularProgressIndicator(
        modifier = Modifier
            .padding(all = 20.dp)
            .size(48.dp),
        strokeWidth = 5.dp
    )
}

@Composable
private fun Failure(
    onClose: () -> Unit,
    error: Throwable
) = BasicDialog(
    onDismissRequest = onClose
) {
    SelectionContainer {
        Text(
            text = error.stackTraceToString(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 10,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(all = 20.dp)
        )
    }
}