package dev.sanmer.github.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class WorkflowRun(
    val id: Long,
    val name: String = "",
    @JsonNames("node_id")
    val nodeId: String,
    @JsonNames("head_branch")
    val headBranch: String,
    @JsonNames("head_sha")
    val headSha: String,
    @JsonNames("run_number")
    val runNumber: Int,
    val event: String,
    val status: String,
    @JsonNames("html_url")
    val htmlUrl: String,
    @JsonNames("created_at")
    val createdAt: Instant,
    @JsonNames("updated_at")
    val updatedAt: Instant,
    val actor: Owner,
    @JsonNames("triggering_actor")
    val triggeringActor: Owner,
    @JsonNames("run_started_at")
    val runStartedAt: Instant,
    @JsonNames("head_commit")
    val headCommit: Commit,
    @JsonNames("display_title")
    val displayTitle: String
)
