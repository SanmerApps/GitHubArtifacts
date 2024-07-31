package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.ktx.toLocalDate
import dev.sanmer.github.artifacts.repository.DbRepository
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
    private val token = savedStateHandle.token
    val edit = token.isNotBlank()

    var input by mutableStateOf(Input())
        private set

    var hidden by mutableStateOf(true)
        private set

    val updatedAt by derivedStateOf {
        input.updatedAt.toLocalDate(TimeZone.currentSystemDefault())
    }

    private val checks = mutableStateMapOf<Check, Boolean>()

    init {
        Timber.d("EditTokenViewModel init")
        tokenObserver()
    }

    private fun tokenObserver() {
        viewModelScope.launch {
            dbRepository.getTokenAsFlow(token)
                .collect { token ->
                    updateInput { Input(token) }
                }
        }
    }

    private fun check(): Boolean {
        Check.Name.check(input.name, checks::put)
        Check.Token.check(input.token, checks::put)
        return checks.all { it.value }
    }

    fun isFailed(value: Check) = !(checks[value] ?: true)

    fun updateInput(block: (Input) -> Input) {
        input = block(input)
    }

    fun updateHidden(block: (Boolean) -> Boolean) {
        hidden = block(hidden)
    }

    fun save(block: () -> Unit = {}) {
        if (!check()) return

        viewModelScope.launch {
            dbRepository.insertToken(input.entity)
            block()
        }
    }

    data class Input(
        val token: String = "",
        val name: String = "",
        val updatedAt: Instant = Clock.System.now()
    ) {
        constructor(token: TokenEntity) : this(
            token = token.token,
            name = token.name,
            updatedAt = token.updatedAt
        )

        val entity get() = TokenEntity(
            token = token.trim(),
            name = name.trim(),
            updatedAt = updatedAt
        )

        fun new() = copy(
            updatedAt = Clock.System.now()
        )
    }

    enum class Check(val ok: (String) -> Boolean) {
        Token(String::isNotBlank),
        Name(String::isNotBlank)
    }

    private inline fun Check.check(value: String, block: (Check, Boolean) -> Unit) {
        block(this, ok(value))
    }

    companion object Util {
        val SavedStateHandle.token: String
            inline get() = checkNotNull(get("token"))
    }
}