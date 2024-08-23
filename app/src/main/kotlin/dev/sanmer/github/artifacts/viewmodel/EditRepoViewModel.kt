package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.GitHubHandler
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.None.asLoadData
import dev.sanmer.github.artifacts.repository.DbRepository
import dev.sanmer.github.response.Repository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditRepoViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id = savedStateHandle.id
    val edit = id != 0L

    var input by mutableStateOf(Input())
        private set

    val tokens = mutableStateListOf<TokenEntity>()

    var data: LoadData<Repository> by mutableStateOf(LoadData.None)
        private set

    private val result = mutableStateMapOf<Value, Boolean>()

    init {
        Timber.d("EditRepoViewModel init")
        repoObserver()
        loadToken()
    }

    private fun repoObserver() {
        viewModelScope.launch {
            dbRepository.getRepoAsFlow(id)
                .collect { repo ->
                    updateInput { Input(repo) }
                }
        }
    }

    private fun loadToken() {
        viewModelScope.launch {
            dbRepository.getTokenAll().apply {
                tokens.addAll(this)
                if (!edit) {
                    updateInput { it.copy(token = first().token) }
                }
            }
        }
    }

    private fun isAllOk(): Boolean {
        Value.Owner.ok(input.owner, result::put)
        Value.Name.ok(input.name, result::put)
        return result.all { it.value }
    }

    fun isError(value: Value) = !(result[value] ?: true)

    fun updateInput(block: (Input) -> Input) {
        input = block(input)
    }

    fun save(block: () -> Unit = {}) {
        if (!isAllOk() || data == LoadData.Loading) return

        viewModelScope.launch {
            data = LoadData.Loading
            data = runCatching {
                GitHubHandler(input.token)
                    .getRepo(
                        owner = input.owner.trim(),
                        name = input.name.trim()
                    )
            }.onSuccess {
                dbRepository.insertRepo(
                    RepoEntity(
                        repo = it,
                        token = input.token
                    )
                )
                block()
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun rewind() {
        data = LoadData.None
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

    enum class Value(val ok: (String) -> Boolean) {
        Owner(String::isNotBlank),
        Name(String::isNotBlank)
    }

    private inline fun Value.ok(value: String, block: (Value, Boolean) -> Unit) {
        block(this, ok(value))
    }

    companion object Default {
        val SavedStateHandle.id: Long
            inline get() = checkNotNull(get("id"))
    }
}