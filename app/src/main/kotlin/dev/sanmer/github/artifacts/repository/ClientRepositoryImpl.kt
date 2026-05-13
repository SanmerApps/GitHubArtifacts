package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.Auth
import dev.sanmer.github.GitHub

class ClientRepositoryImpl : ClientRepository {
    private val clients = hashMapOf<Long, GitHub>()

    override fun new(
        token: String
    ) = GitHub(
        auth = Auth.Bearer(token)
    )

    override fun getOrCreate(id: Long, token: String) = clients.getOrPut(id) {
        GitHub(
            auth = Auth.Bearer(token)
        )
    }

    override fun get(id: Long) = clients.getOrElse(id) { GitHub(auth = Auth.None) }
}