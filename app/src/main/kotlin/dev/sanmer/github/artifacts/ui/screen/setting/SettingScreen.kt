package dev.sanmer.github.artifacts.ui.screen.setting

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.github.artifacts.Const
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ktx.viewUrl
import dev.sanmer.github.artifacts.ui.screen.Screen
import dev.sanmer.github.artifacts.ui.screen.setting.component.SettingIcon
import dev.sanmer.github.artifacts.ui.screen.setting.component.SettingItem

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    goTo: (Screen) -> Unit,
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                onBack = goBack,
                scrollBehavior = scrollBehavior
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(all = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SettingItem(
                icon = {
                    SettingIcon(
                        icon = R.drawable.key,
                        color = if (isSystemInDarkTheme()) {
                            colorResource(id = R.color.material_blue_900)
                        } else {
                            colorResource(id = R.color.material_blue_300)
                        }
                    )
                },
                title = stringResource(id = R.string.settings_token_title),
                text = stringResource(id = R.string.settings_token_desc),
                onClick = { goTo(Screen.Token) }
            )

            SettingItem(
                icon = {
                    SettingIcon(
                        icon = R.drawable.git_branch,
                        color = if (isSystemInDarkTheme()) {
                            colorResource(id = R.color.material_green_900)
                        } else {
                            colorResource(id = R.color.material_green_300)
                        }
                    )
                },
                title = stringResource(id = R.string.settings_repo_title),
                text = stringResource(id = R.string.settings_repo_desc),
                onClick = { goTo(Screen.Repo) },
                enabled = viewModel.hasToken
            )
        }
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.settings_title)) },
    navigationIcon = {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = {
        val context = LocalContext.current

        IconButton(
            onClick = { context.viewUrl(Const.GITHUB_URL) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.brand_github),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)