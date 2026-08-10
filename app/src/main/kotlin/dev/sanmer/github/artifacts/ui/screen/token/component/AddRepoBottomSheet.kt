package dev.sanmer.github.artifacts.ui.screen.token.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.component.DragHandle
import dev.sanmer.github.artifacts.ui.component.Finished
import dev.sanmer.github.artifacts.ui.component.Loading
import dev.sanmer.github.artifacts.ui.ktx.bottom
import dev.sanmer.github.artifacts.ui.ktx.surface
import dev.sanmer.github.artifacts.ui.screen.home.component.RepoItem
import dev.sanmer.github.artifacts.ui.screen.token.EditTokenViewModel

@Composable
fun AddRepoBottomSheet(
    onClose: () -> Unit,
    input: EditTokenViewModel.RepoInput,
    repo: LoadData<Repo>,
    onFetch: () -> Unit,
    onSave: (Repo) -> Unit,
    onRevert: () -> Unit
) = ModalBottomSheet(
    onDismissRequest = { if (!repo.isLoading) onClose() },
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

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        repo.onPending {
            EditRepoItem(
                input = input
            )

            Button(
                onClick = onFetch,
                enabled = input.isNotEmpty,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.edit_fetch))
            }
        }.onLoading {
            Loading(
                modifier = Modifier
                    .height(201.dp)
                    .fillMaxWidth()
            )
        }.onSuccess {
            RepoItem(
                repo = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .surface(
                        shape = MaterialTheme.shapes.large,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder(false)
                    )
                    .padding(all = 15.dp)
            )

            BackHandler(
                onBack = onRevert
            )

            Button(
                onClick = { onSave(it) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.edit_save))
            }
        }.onFailure {
            Finished(
                label = it.message ?: it.javaClass.name,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
            )

            BackHandler(
                onBack = onRevert
            )

            Button(
                onClick = onRevert,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.edit_back))
            }
        }
    }
}