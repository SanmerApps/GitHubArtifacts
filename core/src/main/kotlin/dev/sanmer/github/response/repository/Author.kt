package dev.sanmer.github.response.repository

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val name: String,
    val email: String
) {
    companion object Empty {
        operator fun invoke() =
            Author(
                name = "",
                email = ""
            )
    }
}
