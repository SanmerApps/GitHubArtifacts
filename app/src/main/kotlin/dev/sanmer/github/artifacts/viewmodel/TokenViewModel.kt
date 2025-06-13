package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.artifacts.database.entity.TokenWithRepo
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.getValue
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    var loadData by mutableStateOf<LoadData<List<TokenWithRepo>>>(LoadData.Loading)
        private set
    val tokens inline get() = loadData.getValue(emptyList()) { it }

    init {
        Timber.d("TokenViewModel init")
        dbObserver()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.tokenAndRepoFlow
                .collect { repos ->
                    loadData = LoadData.Success(repos)
                }
        }
    }
}