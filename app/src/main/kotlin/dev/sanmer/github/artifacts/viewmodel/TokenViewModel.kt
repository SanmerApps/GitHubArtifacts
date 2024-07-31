package dev.sanmer.github.artifacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    val tokens get() = dbRepository.tokenAndRepoFlow

    init {
        Timber.d("TokenViewModel init")
    }

    fun delete(token: TokenEntity) {
        viewModelScope.launch {
            dbRepository.deleteToken(token)
        }
    }
}