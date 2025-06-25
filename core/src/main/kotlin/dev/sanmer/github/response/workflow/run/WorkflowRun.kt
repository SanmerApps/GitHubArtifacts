package dev.sanmer.github.response.workflow.run

import dev.sanmer.github.response.repository.Commit
import dev.sanmer.github.response.repository.Owner
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class WorkflowRun(
    val id: Long,
    val name: String = "",
    @SerialName("node_id")
    val nodeId: String,
    @SerialName("head_branch")
    val headBranch: String,
    @SerialName("head_sha")
    val headSha: String,
    @SerialName("run_number")
    val runNumber: Int,
    val event: String,
    val status: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("created_at")
    @Contextual
    val createdAt: Instant,
    @SerialName("updated_at")
    @Contextual
    val updatedAt: Instant,
    val actor: Owner,
    @SerialName("triggering_actor")
    val triggeringActor: Owner,
    @SerialName("run_started_at")
    @Contextual
    val runStartedAt: Instant,
    @SerialName("head_commit")
    val headCommit: Commit,
    @SerialName("display_title")
    val displayTitle: String
)
