package dev.sanmer.github.artifacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.repository.DbRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    val repos = dbRepository.repoAndTokenFlow

    init {
        Timber.d("RepoViewModel init")
    }

    fun delete(repo: RepoEntity) {
        viewModelScope.launch {
            dbRepository.deleteRepo(repo)
        }
    }
}