package dev.sanmer.github.artifacts.ui.screen.token

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.ktx.toLocalDate
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.asLoadData
import dev.sanmer.github.artifacts.repository.ClientRepository
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class EditTokenViewModel(
    private val dbRepository: DbRepository,
    private val clientRepository: ClientRepository,
    private val id: Long
) : ViewModel() {
    val isEdit = id != Long.MAX_VALUE

    val tokenInput = TokenInput()
    val isChanged inline get() = !isEdit || tokenInput.isAnyChanged

    val repoInput = RepoInput()
    var repos by mutableStateOf(emptyList<RepoEntity>())
        private set
    val isDeletable inline get() = isEdit && repos.isEmpty()

    var loadData by mutableStateOf<LoadData<Unit>>(LoadData.Pending)
        private set

    private val logger = Logger.Android("EditTokenViewModel")

    init {
        logger.d("init")
        loadDb()
    }

    private fun loadDb() {
        viewModelScope.launch {
            if (isEdit) {
                dbRepository.getTokenAndReposAsFlow(id)
                    .collect { (token, list) ->
                        tokenInput.update(token)
                        repos = list
                    }
            }
        }
    }

    fun save(block: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                when {
                    isEdit -> dbRepository.updateToken(tokenInput.entity(id))
                    else -> dbRepository.insertToken(tokenInput.entity())
                }
            }.onSuccess {
                block()
            }.onFailure {
                logger.e(it)
            }
        }
    }

    fun delete(block: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                dbRepository.deleteToken(id)
            }.onSuccess {
                block()
            }.onFailure {
                logger.e(it)
            }
        }
    }

    fun addRepo(block: () -> Unit = {}) {
        viewModelScope.launch {
            loadData = LoadData.Loading
            loadData = runCatching {
                val github = clientRepository.getOrCreate(id, tokenInput.tokenValue)
                val repo = github.repositories.get(repoInput.ownerValue, repoInput.nameValue)
                val entity = RepoEntity(id, repo)
                dbRepository.insertRepo(entity)
                repoInput.clear()
            }.onSuccess {
                block()
            }.onFailure {
                logger.e(it)
            }.asLoadData()
        }
    }

    fun deleteRepo(entity: RepoEntity) {
        viewModelScope.launch {
            dbRepository.deleteRepo(entity)
        }
    }

    fun revertLoadData() {
        loadData = LoadData.Pending
    }

    data class TokenInput(
        val token: TextFieldState,
        val name: TextFieldState,
        val createdAt: MutableState<Instant>,
        val lifetime: TextFieldState
    ) {
        constructor(
            token: String = "",
            name: String = "",
            createdAt: Instant = Clock.System.now(),
            lifetime: Long = 90L
        ) : this(
            token = TextFieldState(token),
            name = TextFieldState(name),
            createdAt = mutableStateOf(createdAt),
            lifetime = TextFieldState(lifetime.toString())
        )

        private var entity by mutableStateOf<TokenEntity?>(null)
        private var createdAtValue by createdAt
        private val _isCreatedAtChanged inline get() = createdAtValue != entity?.createdAt
        val tokenValue inline get() = token.text.trim().toString()
        private val _isTokenChanged inline get() = token.text.trim() != entity?.token
        private val nameValue inline get() = name.text.trim().toString()
        private val _isNameChanged inline get() = name.text.trim() != entity?.name
        private val lifetimeValue inline get() = with(lifetime.text.toString()) { if (isNotEmpty()) toLong() else 0 }
        private val _isLifetimeChanged inline get() = lifetime.text != entity?.lifetime.toString()

        val isTokenChanged by derivedStateOf { entity != null && _isTokenChanged }
        val isAnyChanged by derivedStateOf { entity != null && (_isTokenChanged || _isNameChanged || _isCreatedAtChanged || _isLifetimeChanged) }
        val expiredAt by derivedStateOf { (createdAtValue + lifetimeValue.days).toLocalDate(TimeZone.currentSystemDefault()) }

        fun entity(id: Long = 0) = TokenEntity(
            id = id,
            token = tokenValue,
            name = nameValue,
            createdAt = createdAtValue,
            lifetime = lifetimeValue
        )

        fun updateCreatedAt() {
            createdAtValue = Clock.System.now()
        }

        fun revertCreatedAt() {
            entity?.let { createdAtValue = it.createdAt }
        }

        fun update(value: TokenEntity) {
            entity = value
            createdAtValue = value.createdAt
            token.edit {
                delete(0, length)
                append(value.token)
                placeCursorAtEnd()
            }
            name.edit {
                delete(0, length)
                append(value.name)
                placeCursorAtEnd()
            }
            lifetime.edit {
                delete(0, length)
                append(value.lifetime.toString())
                placeCursorAtEnd()
            }
        }
    }

    data class RepoInput(
        val owner: TextFieldState,
        val name: TextFieldState
    ) {
        constructor(
            owner: String = "",
            name: String = ""
        ) : this(
            owner = TextFieldState(owner),
            name = TextFieldState(name)
        )

        val ownerValue inline get() = owner.text.trim().toString()
        val nameValue inline get() = name.text.trim().toString()
        val isNotEmpty inline get() = owner.text.isNotEmpty() && name.text.isNotEmpty()

        fun clear() {
            owner.clearText()
            name.clearText()
        }
    }
}