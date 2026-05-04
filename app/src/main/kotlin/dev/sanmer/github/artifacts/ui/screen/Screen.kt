package dev.sanmer.github.artifacts.ui.screen

import androidx.navigation3.runtime.NavKey
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class Workflow(
        val token: String,
        val owner: String,
        val name: String
    ) : Screen {
        constructor(entity: RepoEntity) : this(
            token = entity.token,
            owner = entity.owner,
            name = entity.name
        )
    }

    @Serializable
    data object Setting : Screen

    @Serializable
    data object Token : Screen

    @Serializable
    data class AddToken(
        val token: String = ""
    ) : Screen

    @Serializable
    data object Repo : Screen

    @Serializable
    data class AddRepo(
        val id: Long = 0L
    ) : Screen
}