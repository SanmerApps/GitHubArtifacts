package dev.sanmer.github.artifacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.GitHubHandler
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    val repos = dbRepository.repoFlow
        .map { repos ->
            repos.sortedByDescending { it.pushedAt }
        }

    private var allSize = -1f
    private val updatedSize = MutableStateFlow(1)
    val progressFlow get() = updatedSize.map { it / allSize }

    init {
        Timber.d("HomeViewModel init")
        updateRepoAll()
    }

    fun updateRepoAll() {
        viewModelScope.launch {
            val olds = dbRepository.getRepoAll()
            if (olds.isEmpty()) return@launch

            allSize = olds.size.toFloat()
            updatedSize.update { 0 }
            val news = olds.map {
                async {
                    getRepo(it).apply { updatedSize.update { it + 1 } }
                }
            }.awaitAll()
                .filterNotNull()

            allSize = -1f
            dbRepository.insertRepo(news)
        }
    }

    private suspend fun getRepo(repo: RepoEntity) =
        runCatching {
            GitHubHandler(repo.token)
                .getRepo(
                    owner = repo.owner,
                    name = repo.name
                ).let {
                    repo.copy(it)
                }
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
}