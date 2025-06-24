package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.Auth.Default.toBearerAuth
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.asLoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.getOrThrow
import dev.sanmer.github.artifacts.repository.ClientRepository
import dev.sanmer.github.artifacts.repository.DbRepository
import dev.sanmer.github.artifacts.ui.main.Screen
import dev.sanmer.github.response.repository.Repository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddRepoViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val addRepo = savedStateHandle.toRoute<Screen.AddRepo>()
    val isEdit get() = addRepo.isEdit

    var input by mutableStateOf(Input())
        private set

    val tokens = mutableStateListOf<TokenEntity>()

    var data: LoadData<Repository> by mutableStateOf(LoadData.Loading)
        private set

    var control by mutableStateOf(Control.Edit)
        private set

    init {
        Timber.d("AddRepoViewModel init")
        dbObserver()
        loadTokens()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.getRepoAsFlow(addRepo.id)
                .collect { repo ->
                    input { Input(repo) }
                }
        }
    }

    private fun loadTokens() {
        viewModelScope.launch {
            val values = dbRepository.getTokenAll()
            if (!isEdit) {
                input { it.copy(token = values.first().token) }
            }
            tokens.addAll(values)
        }
    }

    fun input(block: (Input) -> Input) {
        input = block(input)
    }

    fun update(value: Control) {
        control = value
    }

    fun connect() {
        viewModelScope.launch {
            control = Control.Connecting
            data = runCatching {
                clientRepository.new(
                    auth = input.token.toBearerAuth()
                ).repositories.get(
                    owner = input.owner,
                    name = input.name
                )
            }.onSuccess {
                control = Control.Connected
            }.onFailure {
                control = Control.Closed
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun save() {
        viewModelScope.launch {
            val repo = data.getOrThrow()
            val value = RepoEntity(
                token = input.token,
                repo = repo
            )

            runCatching {
                if (isEdit) {
                    dbRepository.updateRepo(value.copy(id = addRepo.id))
                } else {
                    dbRepository.insertRepo(value)
                }
            }.onSuccess {
                control = Control.Saved
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            runCatching {
                dbRepository.deleteRepoById(addRepo.id)
            }.onSuccess {
                control = Control.Saved
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    data class Input(
        val token: String = "",
        val owner: String = "",
        val name: String = ""
    ) {
        constructor(repo: RepoEntity) : this(
            token = repo.token,
            owner = repo.owner,
            name = repo.name
        )
    }

    enum class Control {
        Edit,
        Connecting,
        Closed,
        Connected,
        Saved;

        val isEdit inline get() = this == Edit
        val isConnecting inline get() = this == Connecting
        val isClosed inline get() = this == Closed
        val isConnected inline get() = this == Connected
        val isSaved inline get() = this == Saved
    }
}