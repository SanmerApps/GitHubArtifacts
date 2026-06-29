package dev.sanmer.github

import androidx.annotation.IntRange
import dev.sanmer.github.query.repository.RepositorySort
import dev.sanmer.github.query.workflow.run.WorkflowRunEvent
import dev.sanmer.github.query.workflow.run.WorkflowRunStatus
import dev.sanmer.github.response.artifact.ArtifactList
import dev.sanmer.github.response.repository.Repository
import dev.sanmer.github.response.repository.RepositoryList
import dev.sanmer.github.response.workflow.WorkflowList
import dev.sanmer.github.response.workflow.run.WorkflowRunList
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHub {
    companion object Default {
        const val API_VERSION = "2026-03-10"
        const val BASE_URL = "https://api.github.com"

        fun String.toBearerAuth() = "Bearer $this"
    }

    @Headers("Accept: application/vnd.github+json")
    @GET("/users/{owner}/repos")
    suspend fun listRepository(
        @Header("Authorization") auth: String,
        @Path("owner") owner: String,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("sort") sort: RepositorySort? = null
    ): RepositoryList

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}")
    suspend fun getRepository(
        @Header("Authorization") auth: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Repository

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/actions/workflows")
    suspend fun listWorkflow(
        @Header("Authorization") auth: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): WorkflowList

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/actions/runs")
    suspend fun listWorkflowRun(
        @Header("Authorization") auth: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("branch") branch: String? = null,
        @Query("event") event: WorkflowRunEvent? = null,
        @Query("status") status: WorkflowRunStatus? = null
    ): WorkflowRunList

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/actions/workflows/{workflow_id}/runs")
    suspend fun listWorkflowRun(
        @Header("Authorization") auth: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("workflow_id") workflowId: Long,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("branch") branch: String? = null,
        @Query("event") event: WorkflowRunEvent? = null,
        @Query("status") status: WorkflowRunStatus? = null
    ): WorkflowRunList

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/actions/artifacts")
    suspend fun listArtifact(
        @Header("Authorization") auth: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): ArtifactList

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/actions/runs/{run_id}/artifacts")
    suspend fun listArtifact(
        @Header("Authorization") auth: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long
    ): ArtifactList
}