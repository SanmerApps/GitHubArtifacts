package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.ktx.isLong
import dev.sanmer.github.artifacts.ktx.toLocalDate
import dev.sanmer.github.artifacts.repository.DbRepository
import dev.sanmer.github.artifacts.ui.main.Screen
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditTokenViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val editToken = savedStateHandle.toRoute<Screen.EditToken>()
    val edit get() = editToken.edit

    var input by mutableStateOf(Input())
        private set

    var hidden by mutableStateOf(true)
        private set

    val createdAt by derivedStateOf {
        input.createdAt.toLocalDate(TimeZone.currentSystemDefault())
    }

    private val tokens = mutableStateListOf<TokenEntity>()
    val isReplaceable by derivedStateOf {
        !edit && tokens.any { input.name == it.name }
    }

    private val result = mutableStateMapOf<Value, Boolean>()

    init {
        Timber.d("EditTokenViewModel init")
        tokenObserver()
        loadToken()
    }

    private fun tokenObserver() {
        viewModelScope.launch {
            dbRepository.getTokenAsFlow(editToken.token)
                .collect { token ->
                    updateInput { Input(token) }
                }
        }
    }

    private fun loadToken() {
        viewModelScope.launch {
            dbRepository.getTokenAll().apply {
                tokens.addAll(this)
            }
        }
    }

    private fun isAllOk(): Boolean {
        Value.Name.ok(input.name, result::put)
        Value.Token.ok(input.token, result::put)
        Value.Lifetime.ok(input.lifetime, result::put)
        return result.all { it.value }
    }

    fun isError(value: Value) = !(result[value] ?: true)

    fun updateInput(block: (Input) -> Input) {
        input = block(input)
    }

    fun updateHidden(block: (Boolean) -> Boolean) {
        hidden = block(hidden)
    }

    fun save(block: () -> Unit = {}) {
        if (!isAllOk()) return

        viewModelScope.launch {
            dbRepository.insertToken(input.entity)
            block()
        }
    }

    fun replace(block: () -> Unit = {}) {
        if (!isAllOk()) return

        viewModelScope.launch {
            val entity = tokens.first { input.name == it.name }
            val new = dbRepository.getRepoAll()
                .filter { it.token == entity.token }
                .map { it.copy(token = input.token) }

            dbRepository.insertRepo(new)
            dbRepository.insertToken(input.entity)
            dbRepository.deleteToken(entity)
            block()
        }
    }

    data class Input(
        val token: String = "",
        val name: String = "",
        val createdAt: Instant = Clock.System.now(),
        val lifetime: String = "90"
    ) {
        constructor(token: TokenEntity) : this(
            token = token.token,
            name = token.name,
            createdAt = token.createdAt,
            lifetime = token.lifetime.toString()
        )

        val entity get() = TokenEntity(
            token = token.trim(),
            name = name.trim(),
            createdAt = createdAt,
            lifetime = lifetime.toLong()
        )
    }

    enum class Value(val ok: (String) -> Boolean) {
        Token(String::isNotBlank),
        Name(String::isNotBlank),
        Lifetime(String::isLong)
    }

    private inline fun Value.ok(value: String, block: (Value, Boolean) -> Unit) {
        block(this, ok(value))
    }
}