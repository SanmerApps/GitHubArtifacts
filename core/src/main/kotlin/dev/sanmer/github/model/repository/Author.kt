package dev.sanmer.github.model.repository

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val name: String = "",
    val email: String = ""
)