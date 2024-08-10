package dev.sanmer.github.artifacts.ui.screen.token

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.viewmodel.EditTokenViewModel

@Composable
fun EditTokenScreen(
    viewModel: EditTokenViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val visualTransformation = remember { PasswordVisualTransformation() }

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
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(vertical = 15.dp, horizontal = 5.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon = R.drawable.tag)

                OutlinedTextField(
                    value = viewModel.input.name,
                    onValueChange = { name ->
                        viewModel.updateInput { it.copy(name = name) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = MaterialTheme.shapes.medium,
                    placeholder = { Text(text = stringResource(id = R.string.edit_name)) },
                    modifier = Modifier.weight(1f),
                    isError = viewModel.isFailed(EditTokenViewModel.Check.Name)
                )

                Spacer(modifier = Modifier.size(48.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(48.dp))

                OutlinedTextField(
                    value = viewModel.input.token,
                    onValueChange = { token ->
                        viewModel.updateInput { it.copy(token = token) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Done
                    ),
                    shape = MaterialTheme.shapes.medium,
                    placeholder = { Text(text = stringResource(id = R.string.edit_token)) },
                    modifier = Modifier.weight(1f),
                    visualTransformation = if (viewModel.hidden) {
                        visualTransformation
                    } else {
                        VisualTransformation.None
                    },
                    readOnly = viewModel.edit,
                    isError = viewModel.isFailed(EditTokenViewModel.Check.Token)
                )

                IconButton(
                    onClick = { viewModel.updateHidden { !it } }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (viewModel.hidden) {
                                R.drawable.eye_closed
                            } else {
                                R.drawable.eye
                            }
                        ),
                        contentDescription = null
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon = R.drawable.hourglass_empty)

                OutlinedTextField(
                    value = viewModel.updatedAt.toString(),
                    onValueChange = {},
                    readOnly = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(48.dp))
            }
        }
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
                id = if (edit) R.string.edit_token_title else R.string.add_token_title
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