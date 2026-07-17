package dev.sanmer.github.artifacts.ui.screen.token

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.GitHub
import dev.sanmer.github.GitHub.Default.toBearerAuth
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.database.model.Token
import dev.sanmer.github.artifacts.ktx.toInstant
import dev.sanmer.github.artifacts.ktx.toLocalDate
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.asLoadData
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlin.time.Clock
import kotlin.time.Instant

class EditTokenViewModel(
    private val dbRepository: DbRepository,
    private val github: GitHub,
    private val tokenId: Long
) : ViewModel() {
    val isEdit = tokenId > 0

    val tokenInput = TokenInput()
    val isChanged inline get() = !isEdit || tokenInput.isAnyChanged

    val repoInput = RepoInput()
    var repos by mutableStateOf(emptyList<Repo>())
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
                dbRepository.getTokenAndReposAsFlow(tokenId)
                    .collect { (token, list) ->
                        tokenInput.update(token)
                        repos = list.sortedByDescending { it.pushedAt }
                    }
            }
        }
    }

    fun save(block: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                val token = Token(
                    id = tokenId.coerceAtLeast(0),
                    token = tokenInput.tokenValue,
                    name = tokenInput.nameValue,
                    expiredAt = tokenInput.expiredAtValue.toInstant()
                )
                dbRepository.upsertToken(token)
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
                dbRepository.deleteToken(tokenId)
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
                val repo = github.getRepository(
                    auth = tokenInput.tokenValue.toBearerAuth(),
                    owner = repoInput.ownerValue,
                    repo = repoInput.nameValue
                )
                dbRepository.upsertRepo(Repo(tokenId, repo))
                repoInput.clear()
            }.onSuccess {
                block()
            }.onFailure {
                logger.e(it)
            }.asLoadData()
        }
    }

    fun deleteRepo(repo: Repo) {
        viewModelScope.launch {
            dbRepository.deleteRepo(repo.id)
        }
    }

    fun revertLoadData() {
        loadData = LoadData.Pending
    }

    data class TokenInput(
        val token: TextFieldState,
        val name: TextFieldState,
        val expiredAt: TextFieldState
    ) {
        constructor(
            token: String = "",
            name: String = "",
            expiredAt: Instant = Clock.System.now(),
        ) : this(
            token = TextFieldState(token),
            name = TextFieldState(name),
            expiredAt = TextFieldState(expiredAt.toLocalDate().format(LocalDate.Formats.ISO_BASIC))
        )

        val tokenValue inline get() = token.text.trim().toString()
        private var _token by mutableStateOf<String?>(null)
        private val _isTokenChanged inline get() = _token != null && token.text.trim() != _token

        val nameValue inline get() = name.text.trim().toString()
        private var _name by mutableStateOf<String?>(null)
        private val _isNameChanged inline get() = _name != null && name.text.trim() != _name

        val expiredAtValue inline get() = LocalDate.Formats.ISO_BASIC.parse(expiredAt.text)
        private var _expiredAt by mutableStateOf<String?>(null)
        private val _isExpiredAtChanged inline get() = _expiredAt != null && expiredAt.text != _expiredAt

        val isAnyChanged by derivedStateOf { _isTokenChanged || _isNameChanged || _isExpiredAtChanged }

        fun update(value: Token) {
            _token = value.token
            token.edit {
                delete(0, length)
                append(value.token)
                placeCursorAtEnd()
            }
            _name = value.name
            name.edit {
                delete(0, length)
                append(value.name)
                placeCursorAtEnd()
            }
            _expiredAt = value.expiredAt.toLocalDate().format(LocalDate.Formats.ISO_BASIC)
            expiredAt.edit {
                delete(0, length)
                append(_expiredAt)
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