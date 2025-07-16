package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.Auth
import dev.sanmer.github.GitHub

class ClientRepositoryImpl : ClientRepository {
    private sealed class Key {
        data class Token(val value: String) : Key()
    }

    private val clients = hashMapOf<Key, GitHub>()

    override fun new(
        token: String
    ) = GitHub(
        auth = Auth.Bearer(token)
    )

    override fun getOrCreate(
        token: String
    ) = clients.getOrPut(Key.Token(token)) {
        GitHub(
            auth = Auth.Bearer(token)
        )
    }
}