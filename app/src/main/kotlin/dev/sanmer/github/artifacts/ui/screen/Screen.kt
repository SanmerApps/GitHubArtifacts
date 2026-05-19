package dev.sanmer.github.artifacts.ui.screen

import androidx.navigation3.runtime.NavKey
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class Workflow(
        val tokenId: Long,
        val owner: String,
        val name: String
    ) : Screen {
        constructor(repo: RepoEntity) : this(
            tokenId = repo.tokenId,
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