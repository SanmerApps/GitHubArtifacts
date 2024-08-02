package dev.sanmer.github.artifacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.GitHubHandler
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.map
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

    init {
        Timber.d("HomeViewModel init")
        updateRepo()
    }

    private fun updateRepo() {
        viewModelScope.launch {
            val news = dbRepository.getRepoAll()
                .map { async { getRepo(it) } }
                .awaitAll()
                .filterNotNull()

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