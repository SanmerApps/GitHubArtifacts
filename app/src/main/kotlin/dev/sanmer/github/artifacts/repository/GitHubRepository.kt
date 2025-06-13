package dev.sanmer.github.artifacts.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import dev.sanmer.github.Auth
import dev.sanmer.github.GitHub
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepository @Inject constructor() {
    private sealed class Key {
        data class Id(val value: Long) : Key()
        data class Token(val value: String) : Key()
    }

    private val github = hashMapOf<Key, GitHub>()
    var currentId by mutableLongStateOf(-1)
        private set

    fun new(
        auth: Auth
    ) = GitHub(
        auth = auth
    )

    fun getOrNew(
        auth: Auth,
        id: Long
    ) = github.getOrPut(Key.Id(id)) {
        github.getOrPut(Key.Token(auth.toString())) {
            GitHub(
                auth = auth
            )
        }
    }

    fun get(id: Long) = github.getValue(Key.Id(id)).also { currentId = id }
    fun drop(id: Long) = github.remove(Key.Id(id))
    fun currentGitHub() = github.getValue(Key.Id(currentId))
}