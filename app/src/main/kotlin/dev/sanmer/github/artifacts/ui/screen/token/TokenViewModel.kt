package dev.sanmer.github.artifacts.ui.screen.token

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.artifacts.database.model.Token
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch

class TokenViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    var data by mutableStateOf<LoadData<List<Token.AndRepos>>>(LoadData.Loading)
        private set

    init {
        Log.d(TAG, "init")
        loadDb()
    }

    private fun loadDb() {
        viewModelScope.launch {
            dbRepository.getTokensAndReposAsFlow()
                .collect { list ->
                    data = LoadData.Success(
                        list.map { (token, repos) ->
                            Token.AndRepos(
                                token = token,
                                repos = repos.sortedByDescending { it.pushedAt }
                            )
                        }.sortedBy { it.token.name }
                    )
                }
        }
    }

    private companion object Default {
        const val TAG = "TokenViewModel"
    }
}