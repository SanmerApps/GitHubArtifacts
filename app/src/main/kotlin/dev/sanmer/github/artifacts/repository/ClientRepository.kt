package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.GitHub

interface ClientRepository {
    fun put(id: Long, token: String)
    fun getOrCreate(id: Long, token: String): GitHub
    fun getOrDefault(id: Long): GitHub
}