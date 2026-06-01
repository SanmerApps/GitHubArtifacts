package dev.sanmer.github.artifacts.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.asLoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.getValue
import dev.sanmer.github.artifacts.repository.ClientRepository
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dbRepository: DbRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {
    var loadData by mutableStateOf<LoadData<List<RepoEntity.AndToken>>>(LoadData.Loading)
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
                .collect {
                    update(it)
                    loadData = LoadData.Success(it)
                }
        }
    }

    fun update(repo: RepoEntity) = updates.getOrDefault(repo.id, LoadData.Pending)

    fun update(repo: RepoEntity, token: TokenEntity) {
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

    private fun update(list: List<RepoEntity.AndToken>) {
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

    private suspend fun getRepo(repo: RepoEntity, token: TokenEntity) =
        runCatching {
            clientRepository.getOrCreate(
                id = token.id,
                token = token.token
            ).repositories.get(
                owner = repo.owner,
                repo = repo.name
            )
        }.onSuccess {
            dbRepository.updateRepo(repo.copy(repo = it))
        }.onFailure {
            logger.e(it)
        }
}