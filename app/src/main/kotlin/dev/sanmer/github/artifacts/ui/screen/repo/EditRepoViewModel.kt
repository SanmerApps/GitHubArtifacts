package dev.sanmer.github.artifacts.ui.screen.repo

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.asLoadData
import dev.sanmer.github.artifacts.repository.ClientRepository
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch

class EditRepoViewModel(
    private val dbRepository: DbRepository,
    private val clientRepository: ClientRepository,
    private val id: Long
) : ViewModel() {
    val isEdit = id != 0L

    val input = Input()

    var tokens by mutableStateOf(emptyList<TokenEntity>())
        private set

    var loadData by mutableStateOf<LoadData<Unit>>(LoadData.Pending)
        private set

    private val logger = Logger.Android("EditRepoViewModel")

    init {
        logger.d("init")
        loadDb()
    }

    private fun loadDb() {
        viewModelScope.launch {
            if (isEdit) {
                input.update(dbRepository.getRepo(id))
                tokens = dbRepository.getTokens()
            } else {
                tokens = dbRepository.getTokens()
                input.tokenIdValue = tokens.first().id
            }
        }
    }

    fun revertLoadData() {
        loadData = LoadData.Pending
    }

    fun save(block: () -> Unit = {}) {
        viewModelScope.launch {
            loadData = LoadData.Loading
            loadData = runCatching {
                val token = tokens.first { it.id == input.tokenIdValue }
                val github = clientRepository.new(token.token)
                val repo = github.repositories.get(input.ownerValue, input.nameValue)
                val entity = RepoEntity(token.id, repo)
                when {
                    isEdit -> dbRepository.updateRepo(entity.copy(id = id))
                    else -> dbRepository.insertRepo(entity)
                }
            }.onSuccess {
                block()
            }.onFailure {
                logger.e(it)
            }.asLoadData()
        }
    }

    fun delete(block: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                dbRepository.deleteRepo(id)
            }.onSuccess {
                block()
            }.onFailure {
                logger.e(it)
            }
        }
    }

    data class Input(
        val tokenId: MutableLongState,
        val owner: TextFieldState,
        val name: TextFieldState
    ) {
        constructor(
            tokenId: Long = 0L,
            owner: String = "",
            name: String = ""
        ) : this(
            tokenId = mutableLongStateOf(tokenId),
            owner = TextFieldState(owner),
            name = TextFieldState(name)
        )

        var tokenIdValue by tokenId
        val ownerValue inline get() = owner.text.trim().toString()
        val nameValue inline get() = name.text.trim().toString()

        fun update(entity: RepoEntity) {
            tokenIdValue = entity.tokenId
            owner.edit {
                delete(0, length)
                append(entity.owner)
                placeCursorAtEnd()
            }
            name.edit {
                delete(0, length)
                append(entity.name)
                placeCursorAtEnd()
            }
        }
    }
}