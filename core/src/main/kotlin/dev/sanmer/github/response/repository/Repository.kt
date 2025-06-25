package dev.sanmer.github.response.repository

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Repository(
    val id: Long,
    @SerialName("node_id")
    val nodeId: String,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    val owner: Owner,
    val private: Boolean,
    @SerialName("html_url")
    val htmlUrl: String,
    val description: String = "",
    val homepage: String = "",
    val language: String = "",
    @SerialName("forks_count")
    val forksCount: Int,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
    @SerialName("watchers_count")
    val watchersCount: Int,
    @SerialName("open_issues_count")
    val openIssuesCount: Int,
    @SerialName("is_template")
    val isTemplate: Boolean,
    @SerialName("has_issues")
    val hasIssues: Boolean,
    val archived: Boolean,
    @SerialName("pushed_at")
    @Contextual
    val pushedAt: Instant,
    @SerialName("created_at")
    @Contextual
    val createdAt: Instant,
    @SerialName("updated_at")
    @Contextual
    val updatedAt: Instant,
    val license: License = License()
)