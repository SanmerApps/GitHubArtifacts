package dev.sanmer.github.artifacts.ui.screen.token

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.ui.component.Title
import dev.sanmer.github.artifacts.ui.component.Value
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.ktx.surface
import dev.sanmer.github.artifacts.ui.screen.Screen
import dev.sanmer.github.artifacts.ui.screen.repo.component.repoType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun EditTokenScreen(
    viewModel: EditTokenViewModel,
    goTo: (Screen) -> Unit,
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                onClose = goBack,
                isDeletable = viewModel.repos.isEmpty(),
                onDelete = { viewModel.delete(goBack) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                onClick = { viewModel.save { if (!viewModel.isEdit) goBack() } },
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
                TokenItem(
                    input = viewModel.input
                )
            }

            items(viewModel.repos) {
                RepoItem(
                    repo = it,
                    onClick = { goTo(Screen.EditRepo(it.id)) }
                )
            }
        }
    }
}

@Composable
private fun TokenItem(
    input: EditTokenViewModel.Input
) = Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp)
) {
    DisposableEffect(input.isTokenChanged) {
        if (input.isTokenChanged) {
            input.createdAtValue = Clock.System.now()
        } else {
            input.revertCreatedAt()
        }
        onDispose {}
    }

    OutlinedTextField(
        state = input.name,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(id = R.string.edit_name)) },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        state = input.token,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(id = R.string.edit_token)) },
        modifier = Modifier.fillMaxWidth()
    )

    val day = stringResource(R.string.edit_day)
    val days = stringResource(R.string.edit_days)
    OutlinedTextField(
        state = input.lifetime,
        inputTransformation = InputTransformation.maxLength(3).then {
            if (!asCharSequence().isDigitsOnly()) {
                revertAllChanges()
            }
        },
        outputTransformation = {
            append(" ${if (length <= 1) day else days}")
            append(" (${input.expiredAt})")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(id = R.string.edit_expiration)) },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun RepoItem(
    repo: RepoEntity,
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
        .fillMaxWidth()
) {
    val updatedAt by remember(repo.id) {
        derivedStateOf {
            repo.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Title(
        title = repo.fullName,
        subtitle = repo.repoType()
    )

    Value(text = updatedAt)
}

@Composable
private fun TopBar(
    isEdit: Boolean,
    onClose: () -> Unit,
    isDeletable: Boolean,
    onDelete: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val isImeVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    if (isEdit) R.string.edit_token_title
                    else R.string.add_token_title
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
                    enabled = isDeletable
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
    onClick: () -> Unit,
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
            painter = painterResource(
                id = R.drawable.device_floppy
            ),
            contentDescription = null
        )
    }
}