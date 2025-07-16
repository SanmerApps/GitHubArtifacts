package dev.sanmer.github.artifacts.ui.screen.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch

class SettingViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    var hasToken by mutableStateOf(false)
        private set

    private val logger = Logger.Android("SettingViewModel")

    init {
        logger.d("init")
        dbObserver()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.getTokensAsFlow()
                .collect { tokens ->
                    hasToken = tokens.isNotEmpty()
                }
        }
    }
}