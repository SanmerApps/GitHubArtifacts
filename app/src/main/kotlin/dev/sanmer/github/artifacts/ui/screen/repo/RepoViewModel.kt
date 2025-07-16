package dev.sanmer.github.artifacts.ui.screen.repo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.database.entity.RepoWithToken
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.getValue
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch

class RepoViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    var loadData by mutableStateOf<LoadData<List<RepoWithToken>>>(LoadData.Loading)
        private set
    val repos inline get() = loadData.getValue(emptyList()) { it }

    private val logger = Logger.Android("RepoViewModel")

    init {
        logger.d("init")
        dbObserver()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.getReposWithTokenAsFlow()
                .collect { repos ->
                    loadData = LoadData.Success(repos)
                }
        }
    }
}