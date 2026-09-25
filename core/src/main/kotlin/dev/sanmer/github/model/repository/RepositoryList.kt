package dev.sanmer.github.model.repository

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RepositoryList(
    val repositories: List<Repository>
)