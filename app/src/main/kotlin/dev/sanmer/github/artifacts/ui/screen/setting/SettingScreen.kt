package dev.sanmer.github.artifacts.ui.screen.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.component.NavigateUpTopBar
import dev.sanmer.github.artifacts.ui.ktx.navigateSingleTopTo
import dev.sanmer.github.artifacts.ui.main.Screen
import dev.sanmer.github.artifacts.ui.screen.setting.component.SettingIcon
import dev.sanmer.github.artifacts.ui.screen.setting.component.SettingItem

@Composable
fun SettingScreen(
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
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
                        color = Color(0xFF64B5F6)
                    )
                },
                title = stringResource(id = R.string.settings_token_title),
                text = stringResource(id = R.string.settings_token_desc),
                onClick = { navController.navigateSingleTopTo(Screen.Token()) }
            )

            SettingItem(
                icon = {
                    SettingIcon(
                        icon = R.drawable.git_branch,
                        color = Color(0xFF81C784)
                    )
                },
                title = stringResource(id = R.string.settings_repo_title),
                text = stringResource(id = R.string.settings_repo_desc),
                onClick = { navController.navigateSingleTopTo(Screen.Repo()) }
            )
        }
    }
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = NavigateUpTopBar(
    title = stringResource(id = R.string.settings_title),
    navController = navController,
    scrollBehavior = scrollBehavior
)