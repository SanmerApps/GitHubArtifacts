package dev.sanmer.github.artifacts.ui.screen.token

import androidx.compose.foundation.text.input.TextFieldState
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
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class EditTokenViewModel(
    private val dbRepository: DbRepository,
    private val id: Long
) : ViewModel() {
    val isEdit = id != Long.MAX_VALUE

    val input = Input()

    var repos by mutableStateOf(emptyList<RepoEntity>())
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
                        input.update(token)
                        repos = list
                    }
            }
        }
    }

    fun save(block: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                when {
                    isEdit -> dbRepository.updateToken(input.entity(id))
                    else -> dbRepository.insertToken(input.entity())
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

    data class Input(
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

        private var initialToken: String? = null
        private var initialCreatedAt: Instant? = null

        var createdAtValue by createdAt
        val tokenValue inline get() = token.text.trim().toString()
        val nameValue inline get() = name.text.trim().toString()
        val lifetimeValue inline get() = with(lifetime.text.toString()) { if (isNotEmpty()) toLong() else 0 }

        val isTokenChanged by derivedStateOf { initialToken != token.text }
        val expiredAt by derivedStateOf { (createdAtValue + lifetimeValue.days).toLocalDate(TimeZone.currentSystemDefault()) }

        fun entity(id: Long = 0) = TokenEntity(
            id = id,
            token = tokenValue,
            name = nameValue,
            createdAt = createdAtValue,
            lifetime = lifetimeValue
        )

        fun revertCreatedAt() {
            initialCreatedAt?.let { createdAtValue = it }
        }

        fun update(entity: TokenEntity) {
            initialToken = entity.token
            initialCreatedAt = entity.createdAt
            createdAtValue = entity.createdAt
            token.edit {
                delete(0, length)
                placeCursorAtEnd()
                append(entity.token)
            }
            name.edit {
                delete(0, length)
                placeCursorAtEnd()
                append(entity.name)
            }
            lifetime.edit {
                delete(0, length)
                placeCursorAtEnd()
                append(entity.lifetime.toString())
            }
        }
    }
}