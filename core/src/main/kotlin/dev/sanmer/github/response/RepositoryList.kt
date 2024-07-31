package dev.sanmer.github.response

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RepositoryList(
    val repositories: List<Repository>
)