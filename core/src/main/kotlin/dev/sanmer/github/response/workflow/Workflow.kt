package dev.sanmer.github.response.workflow

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Workflow(
    val id: Long,
    @SerialName("node_id")
    val nodeId: String,
    val name: String,
    val path: String,
    val state: WorkflowState,
    @SerialName("created_at")
    @Contextual
    val createdAt: Instant,
    @SerialName("updated_at")
    @Contextual
    val updatedAt: Instant,
)