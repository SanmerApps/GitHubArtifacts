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
import kotlinx.coroutines.flow.asStateFlow
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

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Pending)
    val updateState get() = _updateState.asStateFlow()

    init {
        Timber.d("HomeViewModel init")
        updateRepoAll()
    }

    fun updateRepoAll() {
        viewModelScope.launch {
            val olds = dbRepository.getRepoAll()
            if (olds.isEmpty()) return@launch

            _updateState.update { UpdateState.Ready(olds.size) }
            val news = olds.map { repo ->
                async {
                    getRepo(repo).also { result ->
                        _updateState.update { state ->
                            if (result != null) {
                                UpdateState.Updating(state.size, state.succeed + 1, state.failed)
                            } else {
                                UpdateState.Updating(state.size, state.succeed, state.failed + 1)
                            }
                        }
                    }
                }
            }.awaitAll()
                .filterNotNull()

            _updateState.update { UpdateState.Finished(it.size, it.succeed, it.failed) }
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

    sealed class UpdateState {
        abstract val size: Int
        abstract val succeed: Int
        abstract val failed: Int

        data object Pending : UpdateState() {
            override val size = 1
            override val succeed = 0
            override val failed = -1
        }
        data class Ready(
            override val size: Int
        ) : UpdateState() {
            override val succeed = 0
            override val failed = 0
        }
        data class Updating(
            override val size: Int,
            override val succeed: Int,
            override val failed: Int
        ) : UpdateState()
        data class Finished(
            override val size: Int,
            override val succeed: Int,
            override val failed: Int
        ) : UpdateState()

        val finished inline get() = succeed + failed
        val progress inline get() = finished / size.toFloat()

        val isRunning inline get() = this is Ready || this is Updating

    }
}