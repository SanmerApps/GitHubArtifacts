package dev.sanmer.github.artifacts.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.GitHub
import dev.sanmer.github.GitHub.Default.toBearerAuth
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.database.model.Token
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.asLoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.getValue
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dbRepository: DbRepository,
    private val github: GitHub
) : ViewModel() {
    var loadData by mutableStateOf<LoadData<List<Repo.AndToken>>>(LoadData.Loading)
        private set

    val list inline get() = loadData.getValue(emptyList()) { it }

    private val updates = mutableStateMapOf<Long, LoadData<Unit>>()

    private val logger = Logger.Android("HomeViewModel")

    init {
        logger.d("init")
        loadDb()
    }

    private fun loadDb() {
        viewModelScope.launch {
            dbRepository.getReposAndTokenAsFlow()
                .collect { list ->
                    update(list)
                    loadData = LoadData.Success(
                        list.sortedByDescending { it.repo.pushedAt }
                    )
                }
        }
    }

    fun update(repo: Repo) = updates.getOrDefault(repo.id, LoadData.Pending)

    fun update(repo: Repo, token: Token) {
        viewModelScope.launch {
            when (update(repo)) {
                LoadData.Pending, is LoadData.Failure -> {
                    updates[repo.id] = LoadData.Loading
                    updates[repo.id] = getRepo(repo, token).asLoadData {}
                }

                else -> {}
            }
        }
    }

    private fun update(list: List<Repo.AndToken>) {
        viewModelScope.launch {
            list.map { (repo, token) ->
                async {
                    if (repo.id !in updates) {
                        updates[repo.id] = LoadData.Loading
                        updates[repo.id] = getRepo(repo, token).asLoadData {}
                    }
                }
            }.awaitAll()
        }
    }

    private suspend fun getRepo(repo: Repo, token: Token) =
        runCatching {
            github.getRepository(
                auth = token.token.toBearerAuth(),
                owner = repo.owner,
                repo = repo.name
            )
        }.onSuccess {
            dbRepository.upsertRepo(repo.copy(repo = it))
        }.onFailure {
            logger.e(it)
        }
}