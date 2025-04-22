package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.RepoWithToken
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.None.getValue
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    var loadData by mutableStateOf<LoadData<List<RepoWithToken>>>(LoadData.Loading)
        private set
    val repos inline get() = loadData.getValue { emptyList() }

    init {
        Timber.d("RepoViewModel init")
        dbObserver()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.repoAndTokenFlow
                .collect { repos ->
                    loadData = LoadData.Success(repos)
                }
        }
    }

    fun delete(repo: RepoEntity) {
        viewModelScope.launch {
            dbRepository.deleteRepo(repo)
        }
    }
}