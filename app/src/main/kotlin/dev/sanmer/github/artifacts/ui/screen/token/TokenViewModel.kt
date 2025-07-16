package dev.sanmer.github.artifacts.ui.screen.token

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.database.entity.TokenWithRepo
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.getValue
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch

class TokenViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    var loadData by mutableStateOf<LoadData<List<TokenWithRepo>>>(LoadData.Loading)
        private set
    val tokens inline get() = loadData.getValue(emptyList()) { it }

    private val logger = Logger.Android("TokenViewModel")

    init {
        logger.d("init")
        dbObserver()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.getTokensWithReposAsFlow()
                .collect { repos ->
                    loadData = LoadData.Success(repos)
                }
        }
    }
}