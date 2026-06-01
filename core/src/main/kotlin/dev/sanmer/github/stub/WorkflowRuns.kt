package dev.sanmer.github.stub

import androidx.annotation.IntRange
import dev.sanmer.github.query.workflow.run.WorkflowRunEvent
import dev.sanmer.github.query.workflow.run.WorkflowRunStatus
import dev.sanmer.github.response.workflow.run.WorkflowRunList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkflowRuns {
    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/actions/runs")
    suspend fun list(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("branch") branch: String? = null,
        @Query("event") event: WorkflowRunEvent? = null,
        @Query("status") status: WorkflowRunStatus? = null
    ): WorkflowRunList

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/actions/workflows/{workflow_id}/runs")
    suspend fun list(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("workflow_id") workflowId: Long,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("branch") branch: String? = null,
        @Query("event") event: WorkflowRunEvent? = null,
        @Query("status") status: WorkflowRunStatus? = null
    ): WorkflowRunList
}