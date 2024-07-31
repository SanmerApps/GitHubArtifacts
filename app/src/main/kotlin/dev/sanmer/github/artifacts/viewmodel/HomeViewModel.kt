package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.GitHubHandler
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.repository.DbRepository
import dev.sanmer.github.response.Repository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    private val cache = mutableStateMapOf<Long, Repository>()
    val repos get() = cache.values.sortedByDescending { it.pushedAt }

    var isEmpty by mutableStateOf(false)
        private set

    init {
        Timber.d("HomeViewModel init")
        repoObserver()
    }

    private fun repoObserver() {
        viewModelScope.launch {
            dbRepository.repoFlow
                .distinctUntilChanged()
                .collect { repos ->
                    repos.forEach { getRepo(it) }
                    isEmpty = repos.isEmpty()
                }
        }
    }

    private suspend fun getRepo(repo: RepoEntity) =
        runCatching {
            GitHubHandler(repo.token)
                .getRepo(
                    owner = repo.owner,
                    name = repo.name
                )
        }.onSuccess {
            cache[repo.id] = it
            dbRepository.insertRepo(repo.copy(it))
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
}