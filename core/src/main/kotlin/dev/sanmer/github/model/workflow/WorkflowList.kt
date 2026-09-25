package dev.sanmer.github.model.workflow

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkflowList(
    @SerialName("total_count")
    val totalCount: Int,
    val workflows: List<Workflow>
)
