package dev.sanmer.github.artifacts.ui.screen.token.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.github.artifacts.database.entity.TokenWithRepo
import dev.sanmer.github.artifacts.ui.ktx.navigateSingleTopTo
import dev.sanmer.github.artifacts.ui.ktx.plus
import dev.sanmer.github.artifacts.ui.main.Screen

@Composable
fun TokenList(
    tokens: List<TokenWithRepo>,
    navController: NavController,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(),
    state = state,
    contentPadding = contentPadding + PaddingValues(all = 15.dp),
    verticalArrangement = Arrangement.spacedBy(15.dp)
) {
    items(tokens) {
        TokenItem(
            token = it,
            onClick = { navController.navigateSingleTopTo(Screen.AddToken(it.token.token)) }
        )
    }
}