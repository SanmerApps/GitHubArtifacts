package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.Auth
import dev.sanmer.github.GitHub

class ClientRepositoryImpl : ClientRepository, MutableMap<Long, GitHub> by hashMapOf() {
    override fun put(id: Long, token: String) {
        put(id, GitHub(auth = Auth.Bearer(token)))
    }

    override fun getOrCreate(id: Long, token: String) = getOrPut(id) {
        GitHub(auth = Auth.Bearer(token))
    }

    override fun getOrDefault(id: Long) = getOrElse(id) { GitHub(auth = Auth.None) }
}