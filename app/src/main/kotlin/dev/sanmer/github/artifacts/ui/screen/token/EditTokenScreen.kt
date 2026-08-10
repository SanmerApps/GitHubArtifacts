package dev.sanmer.github.artifacts.ui.screen.token

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.ktx.isScrollingUp
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.screen.Screen
import dev.sanmer.github.artifacts.ui.screen.token.EditTokenViewModel.BottomSheet
import dev.sanmer.github.artifacts.ui.screen.token.component.AddRepoBottomSheet
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

    when (val bs = viewModel.bottomSheet) {
        BottomSheet.None -> {}
        is BottomSheet.AddRepo -> AddRepoBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            input = viewModel.repoInput,
            repo = bs.repo,
            onFetch = viewModel::fetchRepo,
            onSave = viewModel::saveRepo,
            onRevert = { viewModel.bottomSheet = BottomSheet.AddRepo(LoadData.Pending) }
        )
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                onBack = goBack,
                isRepoEmpty = viewModel.isRepoEmpty,
                onDelete = { viewModel.deleteToken(goBack) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                isChanged = viewModel.isChanged,
                onSave = { viewModel.saveToken(goBack) },
                onAdd = { viewModel.bottomSheet = BottomSheet.AddRepo(LoadData.Pending) },
                visible = viewModel.bottomSheet == BottomSheet.None && isScrollingUp
            )
        }
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding + PaddingValues(all = 15.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
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
                    onWorkflow = { goTo(Screen.Workflow(viewModel.tokenInput.tokenValue, it)) }
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    isEdit: Boolean,
    onBack: () -> Unit,
    isRepoEmpty: Boolean,
    onDelete: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(
            text = stringResource(
                if (isEdit) R.string.edit_token_title
                else R.string.add_token_title
            )
        )
    },
    navigationIcon = {
        val keyboardController = LocalSoftwareKeyboardController.current
        IconButton(
            onClick = {
                keyboardController?.hide()
                onBack()
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
                enabled = isRepoEmpty
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

@Composable
private fun ActionButton(
    isChanged: Boolean,
    onSave: () -> Unit,
    onAdd: () -> Unit,
    visible: Boolean = true
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + scaleIn(),
    exit = scaleOut() + fadeOut()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    FloatingActionButton(
        onClick = {
            keyboardController?.hide()
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