package dev.sanmer.github.artifacts.ui.screen.token

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.DragHandle
import dev.sanmer.github.artifacts.ui.component.Finished
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.artifacts.ui.ktx.bottom
import dev.sanmer.github.artifacts.ui.ktx.isScrollingUp
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.screen.Screen
import dev.sanmer.github.artifacts.ui.screen.token.component.EditRepoItem
import dev.sanmer.github.artifacts.ui.screen.token.component.EditTokenItem
import dev.sanmer.github.artifacts.ui.screen.token.component.RepoItem

@Composable
fun EditTokenScreen(
    viewModel: EditTokenViewModel,
    goTo: (Screen) -> Unit,
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

    val (add, setAdd) = remember { mutableStateOf(false) }
    if (add) AddRepoBottomSheet(
        input = viewModel.repoInput,
        data = viewModel.loadData,
        onClose = { setAdd(false) },
        onSave = { viewModel.addRepo { setAdd(false) } },
        onRevert = viewModel::revertLoadData
    )

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                onClose = goBack,
                isDeletable = viewModel.isDeletable,
                onDelete = { viewModel.delete(goBack) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isScrollingUp,
                enter = fadeIn() + scaleIn(),
                exit = scaleOut() + fadeOut()
            ) {
                ActionButton(
                    isChanged = viewModel.isChanged,
                    onSave = { viewModel.save { if (!viewModel.isEdit) goBack() } },
                    onAdd = { setAdd(true) }
                )
            }
        }
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding + PaddingValues(all = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            item {
                EditTokenItem(
                    input = viewModel.tokenInput
                )
            }

            items(
                items = viewModel.repos,
                key = { it.id }
            ) {
                RepoItem(
                    repo = it,
                    onDelete = { viewModel.deleteRepo(it) },
                    onWorkflow = { goTo(Screen.Workflow(it)) }
                )
            }
        }
    }
}

@Composable
private fun AddRepoBottomSheet(
    input: EditTokenViewModel.RepoInput,
    data: LoadData<Unit>,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onRevert: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    DisposableEffect(true) {
        onDispose(onRevert)
    }

    ModalBottomSheet(
        onDismissRequest = {
            keyboardController?.hide()
            if (!data.isLoading) onClose()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.large.bottom(0.dp),
        dragHandle = null
    ) {
        DragHandle()

        Text(
            text = stringResource(R.string.add_repo_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Crossfade(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(all = 15.dp),
            targetState = data
        ) {
            when (it) {
                LoadData.Loading -> Loading(
                    modifier = Modifier
                        .height(138.dp)
                        .fillMaxWidth()
                )

                is LoadData.Failure -> Finished(
                    label = it.error.message ?: it.error.javaClass.name,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .height(138.dp)
                        .fillMaxWidth(),
                )

                else -> EditRepoItem(
                    input = input
                )
            }
        }

        Button(
            onClick = {
                keyboardController?.hide()
                if (data.isFailure) onRevert() else onSave()
            },
            enabled = input.isNotEmpty && !data.isLoading,
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(
                    when {
                        data.isFailure -> R.string.edit_back
                        else -> R.string.edit_save
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
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
                    painter = painterResource(R.drawable.arrow_left),
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
    isChanged: Boolean,
    onSave: () -> Unit,
    onAdd: () -> Unit
) {
    val isImeVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    FloatingActionButton(
        onClick = {
            if (isImeVisible) keyboardController?.hide()
            when {
                isChanged -> onSave()
                else -> onAdd()
            }
        }
    ) {
        Icon(
            painter = painterResource(
                when {
                    isChanged -> R.drawable.device_floppy
                    else -> R.drawable.plus
                }
            ),
            contentDescription = null
        )
    }
}