package dev.sanmer.github.artifacts.database.entity

import androidx.room.Entity
import dev.sanmer.github.response.Repository
import kotlinx.datetime.Instant

@Entity(tableName = "repo", primaryKeys = ["id"])
data class RepoEntity(
    val token: String,
    val id: Long,
    val owner: String,
    val name: String,
    val private: Boolean,
    val isTemplate: Boolean,
    val archived: Boolean,
    val updatedAt: Instant
) {
    constructor(token: String, repo: Repository) : this(
        token = token,
        id = repo.id,
        owner = repo.owner.login,
        name = repo.name,
        private = repo.private,
        isTemplate = repo.isTemplate,
        archived = repo.archived,
        updatedAt = repo.pushedAt
    )

    val fullName inline get() = "$owner/$name"

    fun copy(repo: Repository) = copy(
        id = repo.id,
        owner = repo.owner.login,
        name = repo.name,
        private = repo.private,
        isTemplate = repo.isTemplate,
        archived = repo.archived,
        updatedAt = repo.pushedAt
    )
}
