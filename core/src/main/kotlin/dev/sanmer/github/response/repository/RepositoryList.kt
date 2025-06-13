package dev.sanmer.github.response.repository

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RepositoryList(
    val repositories: List<Repository>
)