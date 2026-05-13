package dev.sanmer.github.artifacts.ui.screen.repo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.getValue
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch

class RepoViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    var loadData by mutableStateOf<LoadData<List<RepoEntity.AndToken>>>(LoadData.Loading)
        private set

    val list inline get() = loadData.getValue(emptyList()) { it }

    private val logger = Logger.Android("RepoViewModel")

    init {
        logger.d("init")
        loadDb()
    }

    private fun loadDb() {
        viewModelScope.launch {
            dbRepository.getReposAndTokenAsFlow()
                .collect {
                    loadData = LoadData.Success(it)
                }
        }
    }
}