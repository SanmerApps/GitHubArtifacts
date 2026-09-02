package dev.sanmer.github.artifacts.ui.screen.token.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import dev.sanmer.github.artifacts.ui.component.DragHandle
import dev.sanmer.github.artifacts.ui.ktx.bottom
import dev.sanmer.github.artifacts.ui.ktx.surface
import dev.sanmer.github.artifacts.ui.screen.home.component.RepoItem

@Composable
fun ViewRepoBottomSheet(
    onClose: () -> Unit,
    repo: Repo,
    onWorkflow: () -> Unit,
    onDelete: () -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = MaterialTheme.shapes.large.bottom(0.dp),
    dragHandle = null
) {
    DragHandle()

    Text(
        text = stringResource(R.string.view_repo_title),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    RepoItem(
        repo = repo,
        modifier = Modifier
            .padding(all = 15.dp)
            .fillMaxWidth()
            .surface(
                shape = MaterialTheme.shapes.large,
                backgroundColor = MaterialTheme.colorScheme.surface,
                border = CardDefaults.outlinedCardBorder(false)
            )
            .clickable(
                onClick = {
                    onWorkflow()
                    onClose()
                }
            )
            .padding(all = 15.dp)
    )

    Button(
        onClick = {
            onDelete()
            onClose()
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
            .fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.edit_delete))
    }
}