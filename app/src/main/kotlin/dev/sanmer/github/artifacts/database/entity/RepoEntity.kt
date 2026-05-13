package dev.sanmer.github.artifacts.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import dev.sanmer.github.response.repository.Repository
import kotlin.time.Instant

@Entity(tableName = "repo")
data class RepoEntity(
    @PrimaryKey
    val id: Long,
    val tokenId: Long,
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
    constructor(tokenId: Long, repo: Repository) : this(
        tokenId = tokenId,
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

    fun copy(repo: Repository) = RepoEntity(
        tokenId = tokenId,
        repo = repo
    )

    data class AndToken(
        @Embedded
        val repo: RepoEntity,
        @Relation(parentColumn = "tokenId", entityColumn = "id")
        val token: TokenEntity
    )
}
