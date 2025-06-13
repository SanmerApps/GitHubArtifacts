package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.ktx.toLocalDate
import dev.sanmer.github.artifacts.repository.DbRepository
import dev.sanmer.github.artifacts.ui.main.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddTokenViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val addToken = savedStateHandle.toRoute<Screen.AddToken>()
    val isEdit get() = addToken.isEdit

    var input by mutableStateOf(Input())
        private set

    var hidden by mutableStateOf(true)
        private set

    val createdAt by derivedStateOf {
        input.createdAt.toLocalDate(TimeZone.currentSystemDefault())
    }

    private val tokens = mutableStateListOf<TokenEntity>()

    var control by mutableStateOf(Control.Edit)
        private set

    var isDeletable by mutableStateOf(true)
        private set

    init {
        Timber.d("AddTokenViewModel init")
        dbObserver()
        loadTokens()
        inputObserver()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.getTokenAsFlow(addToken.token)
                .collect { token ->
                    input { Input(token) }
                }
        }
    }

    private fun loadTokens() {
        viewModelScope.launch {
            if (isEdit) {
                isDeletable = dbRepository.getTokenWithRepo(addToken.token).repo.isEmpty()
            }

            val values = dbRepository.getTokenAll()
            tokens.addAll(values)
        }
    }

    private fun inputObserver() {
        viewModelScope.launch {
            snapshotFlow { input }
                .collectLatest { input ->
                    control = if (tokens.any { input.name == it.name }) {
                        Control.Replace
                    } else {
                        Control.Edit
                    }
                }
        }
    }

    fun input(block: (Input) -> Input) {
        input = block(input)
    }

    fun update(value: Boolean) {
        hidden = value
    }

    fun save() {
        viewModelScope.launch {
            runCatching {
                require(input.name.isNotEmpty()) { "Name is empty" }
                require(input.token.isNotEmpty()) { "Token is empty" }

                val value = TokenEntity(
                    token = input.token.trim(),
                    name = input.name.trim(),
                    createdAt = input.createdAt,
                    lifetime = input.lifetime.toLong()
                )
                if (isEdit) {
                    dbRepository.updateToken(value)
                } else {
                    dbRepository.insertToken(value)
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
                dbRepository.deleteToken(input.token)
            }.onSuccess {
                control = Control.Saved
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun replace() {
        viewModelScope.launch {
            runCatching {
                require(input.token.isNotEmpty()) { "Token is empty" }

                val old = tokens.first { input.name == it.name }
                val new = TokenEntity(
                    token = input.token.trim(),
                    name = input.name.trim(),
                    createdAt = input.createdAt,
                    lifetime = input.lifetime.toLong()
                )

                dbRepository.insertToken(new)
                dbRepository.deleteToken(old)
                dbRepository.updateRepo(
                    dbRepository.getRepoAll()
                        .filter { it.token == old.token }
                        .map { it.copy(token = input.token) }

                )
            }.onSuccess {
                control = Control.Saved
            }.onFailure {
                Timber.e(it)
            }
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
    }

    enum class Control {
        Edit,
        Replace,
        Saved;

        val isEdit inline get() = this == Edit
        val isReplace inline get() = this == Replace
        val isSaved inline get() = this == Saved
    }
}
