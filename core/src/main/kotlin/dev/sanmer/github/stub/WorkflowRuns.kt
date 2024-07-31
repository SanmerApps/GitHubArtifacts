package dev.sanmer.github.stub

import dev.sanmer.github.response.ArtifactList
import dev.sanmer.github.response.WorkflowRunList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkflowRuns {
    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{name}/actions/runs")
    suspend fun list(
        @Path("owner") owner: String,
        @Path("name") name: String,
        @Query("event") event: String,
        @Query("status") status: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): WorkflowRunList

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{name}/actions/runs/{run_id}/artifacts")
    suspend fun getArtifacts(
        @Path("owner") owner: String,
        @Path("name") name: String,
        @Path("run_id") runId: Long
    ): ArtifactList
}