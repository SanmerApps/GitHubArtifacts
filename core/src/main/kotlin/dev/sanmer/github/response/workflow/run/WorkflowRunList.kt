package dev.sanmer.github.response.workflow.run

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkflowRunList(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("workflow_runs")
    val workflowRuns: List<WorkflowRun>
)
