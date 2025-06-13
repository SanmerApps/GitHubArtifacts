package dev.sanmer.github.artifacts.ui.screen.token

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import dev.sanmer.github.artifacts.viewmodel.AddTokenViewModel
import dev.sanmer.github.artifacts.viewmodel.AddTokenViewModel.Control

@Composable
fun AddTokenScreen(
    viewModel: AddTokenViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(viewModel.control) {
        if (viewModel.control.isSaved) {
            navController.navigateUp()
        }
        onDispose {}
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                isDeletable = viewModel.isDeletable,
                onDelete = viewModel::delete,
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                control = viewModel.control,
                onSave = viewModel::save,
                onReplace = viewModel::replace
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(vertical = 15.dp, horizontal = 5.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            NameTextField(
                value = viewModel.input.name,
                onValueChange = { name ->
                    viewModel.input { it.copy(name = name) }
                }
            )

            TokenTextField(
                value = viewModel.input.token,
                onValueChange = { token ->
                    viewModel.input { it.copy(token = token) }
                },
                hidden = viewModel.hidden,
                onHiddenChange = viewModel::update,
                readOnly = viewModel.isEdit
            )

            CreatedTextField(
                value = viewModel.createdAt.toString(),
                onValueChange = {},
                readOnly = true
            )

            LifetimeTextField(
                value = viewModel.input.lifetime,
                onValueChange = { lifetime ->
                    viewModel.input { it.copy(lifetime = lifetime) }
                },
                readOnly = viewModel.isEdit
            )
        }
    }
}

@Composable
private fun NameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(icon = R.drawable.tag)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(id = R.string.edit_name)) },
        modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.size(48.dp))
}

@Composable
private fun TokenTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hidden: Boolean,
    onHiddenChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    val visualTransformation = remember { PasswordVisualTransformation() }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.size(48.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Next
            ),
            shape = MaterialTheme.shapes.medium,
            label = { Text(text = stringResource(id = R.string.edit_token)) },
            visualTransformation = if (hidden) {
                visualTransformation
            } else {
                VisualTransformation.None
            },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = { onHiddenChange(!hidden) }
        ) {
            Icon(
                painter = painterResource(
                    id = if (hidden) R.drawable.eye_closed else R.drawable.eye
                ),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CreatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(icon = R.drawable.calendar_event)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.size(48.dp))
}

@Composable
private fun LifetimeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(icon = R.drawable.hourglass_empty)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.size(48.dp))
}

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

@Composable
private fun TopBar(
    isEdit: Boolean,
    isDeletable: Boolean,
    onDelete: () -> Unit,
    navController: NavController,
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
                    navController.navigateUp()
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
    control: Control,
    onSave: () -> Unit,
    onReplace: () -> Unit
) {
    val isImeVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    FloatingActionButton(
        onClick = {
            if (isImeVisible) keyboardController?.hide()
            if (control.isReplace) onReplace() else onSave()
        }
    ) {
        Icon(
            painter = painterResource(
                id = if (control.isReplace) R.drawable.replace else R.drawable.device_floppy
            ),
            contentDescription = null
        )
    }
}