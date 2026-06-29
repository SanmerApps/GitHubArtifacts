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
        constructor(token: String, repo: RepoEntity) : this(
            token = token,
            owner = repo.owner,
            name = repo.name
        )
    }

    @Serializable
    data object Token : Screen

    @Serializable
    data class EditToken(
        val id: Long = Long.MAX_VALUE
    ) : Screen
}