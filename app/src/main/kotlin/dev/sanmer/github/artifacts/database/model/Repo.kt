package dev.sanmer.github.artifacts.database.model

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Relation
import dev.sanmer.github.response.repository.Repository
import kotlin.time.Instant

@Entity
data class Repo(
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
        license = repo.license.spdxId
    )

    fun copy(repo: Repository) = Repo(
        tokenId = tokenId,
        repo = repo
    )

    data class AndToken(
        @Embedded
        val repo: Repo,
        @Relation(parentColumns = ["tokenId"], entityColumns = ["id"])
        val token: Token
    )
}
