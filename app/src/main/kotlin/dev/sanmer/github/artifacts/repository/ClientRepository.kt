package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.Auth
import dev.sanmer.github.GitHub
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepository @Inject constructor() {
    private sealed class Key {
        data class Token(val value: String) : Key()
    }

    private val clients = hashMapOf<Key, GitHub>()

    fun new(
        auth: Auth
    ) = GitHub(
        auth = auth
    )

    fun getOrNew(
        auth: Auth
    ) = clients.getOrPut(Key.Token(auth.toString())) {
        GitHub(
            auth = auth
        )
    }
}