package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.GitHub

interface ClientRepository {
    fun new(token: String): GitHub
    fun getOrCreate(id: Long, token: String): GitHub
    fun get(id: Long): GitHub
}