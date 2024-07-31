package dev.sanmer.github.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class WorkflowRunList(
    @JsonNames("total_count")
    val totalCount: Int,
    @JsonNames("workflow_runs")
    val workflowRuns: List<WorkflowRun>
)
