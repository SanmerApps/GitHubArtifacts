package dev.sanmer.github.artifacts.database.entity

import androidx.room.Entity
import dev.sanmer.github.response.Repository
import kotlinx.datetime.Instant

@Entity(tableName = "repo", primaryKeys = ["id"])
data class RepoEntity(
    val token: String,
    val id: Long,
    val name: String,
    val fullName: String,
    val owner: String,
    val private: Boolean,
    val description: String,
    val language: String,
    val forksCount: Int,
    val stargazersCount: Int,
    val watchersCount: Int,
    val openIssuesCount: Int,
    val isTemplate: Boolean,
    val hasIssues: Boolean,
    val archived: Boolean,
    val pushedAt: Instant,
    val updatedAt: Instant,
    val license: String
) {
    constructor(token: String, repo: Repository) : this(
        token = token,
        id = repo.id,
        name = repo.name,
        fullName = repo.fullName,
        owner = repo.owner.login,
        private = repo.private,
        description = repo.description,
        language = repo.language,
        forksCount = repo.forksCount,
        stargazersCount = repo.stargazersCount,
        watchersCount = repo.watchersCount,
        openIssuesCount = repo.openIssuesCount,
        isTemplate = repo.isTemplate,
        hasIssues = repo.hasIssues,
        archived = repo.archived,
        pushedAt = repo.pushedAt,
        updatedAt = repo.updatedAt,
        license = repo.license.name
    )

    fun copy(repo: Repository) =
        RepoEntity(
            token = token,
            repo = repo
        )
}
