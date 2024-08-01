package dev.sanmer.github.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Repository(
    val id: Long,
    @JsonNames("node_id")
    val nodeId: String,
    val name: String,
    @JsonNames("full_name")
    val fullName: String,
    val owner: Owner,
    val private: Boolean,
    @JsonNames("html_url")
    val htmlUrl: String,
    val description: String = "",
    val homepage: String = "",
    val language: String = "",
    @JsonNames("forks_count")
    val forksCount: Int,
    @JsonNames("stargazers_count")
    val stargazersCount: Int,
    @JsonNames("watchers_count")
    val watchersCount: Int,
    @JsonNames("open_issues_count")
    val openIssuesCount: Int,
    @JsonNames("is_template")
    val isTemplate: Boolean,
    @JsonNames("has_issues")
    val hasIssues: Boolean,
    val archived: Boolean,
    @JsonNames("pushed_at")
    val pushedAt: Instant = Instant.fromEpochSeconds(0L),
    @JsonNames("created_at")
    val createdAt: Instant = Instant.fromEpochSeconds(0L),
    @JsonNames("updated_at")
    val updatedAt: Instant = Instant.fromEpochSeconds(0L),
    val license: License = License.Empty()
)