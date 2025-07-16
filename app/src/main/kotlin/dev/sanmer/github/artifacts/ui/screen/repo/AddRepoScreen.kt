package dev.sanmer.github.artifacts.ui.screen.repo

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.github.JsonCompat.encodeJson
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.ui.screen.repo.AddRepoViewModel.Control
import dev.sanmer.github.response.repository.Repository
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddRepoScreen(
    viewModel: AddRepoViewModel = koinViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(viewModel.control) {
        if (viewModel.control.isSaved) {
            navController.navigateUp()
        }
        onDispose {}
    }

    BackHandler(
        enabled = with(viewModel.control) { isClosed || isConnected },
        onBack = { viewModel.update(Control.Edit) }
    )

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                control = viewModel.control,
                setControl = viewModel::update,
                onDelete = viewModel::delete,
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                control = viewModel.control,
                setControl = viewModel::update,
                onConnect = viewModel::connect,
                onSave = viewModel::save
            )
        }
    ) { contentPadding ->
        Crossfade(
            targetState = viewModel.control.isEdit || viewModel.control.isConnecting
        ) { isLoading ->
            if (isLoading) {
                AddRepoContent(
                    viewModel = viewModel,
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(contentPadding)
                )
            } else {
                ConnectContent(
                    viewModel = viewModel,
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(contentPadding)
                )
            }
        }
    }
}

@Composable
private fun ConnectContent(
    viewModel: AddRepoViewModel,
    modifier: Modifier = Modifier
) {
    when (val data = viewModel.data) {
        LoadData.Pending, LoadData.Loading -> {}
        is LoadData.Success<Repository> -> TextCard(
            value = data.value.encodeJson(pretty = true),
            modifier = modifier
        )

        is LoadData.Failure -> TextCard(
            value = data.error.stackTraceToString(),
            modifier = modifier
        )
    }
}

@Composable
private fun TextCard(
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(all = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .border(
                    border = CardDefaults.outlinedCardBorder(),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(all = 20.dp),
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AddRepoContent(
    viewModel: AddRepoViewModel,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    AnimatedVisibility(
        visible = viewModel.control.isConnecting
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .height(5.dp)
                .padding(horizontal = 2.dp)
                .fillMaxWidth()
        )
    }

    OwnerTextField(
        value = viewModel.input.owner,
        onValueChange = { owner ->
            viewModel.input { it.copy(owner = owner) }
        },
        modifier = Modifier.padding(vertical = 15.dp, horizontal = 5.dp),
        readOnly = viewModel.isEdit
    )

    NameTextField(
        value = viewModel.input.name,
        onValueChange = { name ->
            viewModel.input { it.copy(name = name) }
        },
        modifier = Modifier.padding(horizontal = 5.dp),
        readOnly = viewModel.isEdit
    )

    TokensItem(
        tokens = viewModel.tokens,
        selected = viewModel.input.token,
        onValueChange = { token ->
            viewModel.input { it.copy(token = token) }
        },
        modifier = Modifier.padding(vertical = 15.dp, horizontal = 5.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    )
}

@Composable
private fun OwnerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(icon = R.drawable.user)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(id = R.string.edit_owner)) },
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.size(48.dp))
}

@Composable
private fun NameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    Spacer(modifier = Modifier.size(48.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(id = R.string.edit_name)) },
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.size(48.dp))
}

@Composable
private fun TokensItem(
    tokens: List<TokenEntity>,
    selected: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) = Column(
    modifier = modifier,
    verticalArrangement = verticalArrangement
) {
    tokens.forEachIndexed { index, token ->
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
                selected = selected == token.token,
                onClick = { onValueChange(token.token) },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.size(48.dp))
        }
    }
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
            painter = painterResource(id = R.drawable.circle_check_filled),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TopBar(
    isEdit: Boolean,
    control: Control,
    setControl: (Control) -> Unit,
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
                    if (isEdit) R.string.edit_repo_title
                    else R.string.add_repo_title
                )
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (control.isClosed || control.isConnected) {
                        setControl(Control.Edit)
                    } else {
                        if (isImeVisible) keyboardController?.hide()
                        navController.navigateUp()
                    }
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.x),
                    contentDescription = null
                )
            }
        },
        actions = {
            if (isEdit && control.isEdit) {
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
    control: Control,
    setControl: (Control) -> Unit,
    onConnect: () -> Unit,
    onSave: () -> Unit
) {
    val isImeVisible = WindowInsets.isImeVisible
    val keyboardController = LocalSoftwareKeyboardController.current

    FloatingActionButton(
        onClick = {
            if (isImeVisible) keyboardController?.hide()
            when (control) {
                Control.Edit -> onConnect()
                Control.Connecting -> {}
                Control.Closed -> setControl(Control.Edit)
                Control.Connected -> onSave()
                Control.Saved -> {}
            }
        }
    ) {
        Icon(
            painter = painterResource(
                when (control) {
                    Control.Edit -> R.drawable.plug_connected
                    Control.Connecting -> R.drawable.plug_connected
                    Control.Closed -> R.drawable.plug_connected_x
                    Control.Connected -> R.drawable.device_floppy
                    Control.Saved -> R.drawable.device_floppy
                }
            ),
            contentDescription = null
        )
    }
}