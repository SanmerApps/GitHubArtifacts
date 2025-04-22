package dev.sanmer.github.artifacts.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    var hasToken by mutableStateOf(false)
        private set

    init {
        Timber.d("SettingViewModel init")
        dbObserver()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.tokenFlow
                .collect { tokens ->
                    hasToken = tokens.isNotEmpty()
                }
        }
    }
}