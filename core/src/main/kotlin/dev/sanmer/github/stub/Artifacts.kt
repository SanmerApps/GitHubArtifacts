package dev.sanmer.github.stub

import androidx.annotation.IntRange
import dev.sanmer.github.response.artifact.ArtifactList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Artifacts {
    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/actions/artifacts")
    suspend fun list(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): ArtifactList

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/actions/runs/{run_id}/artifacts")
    suspend fun list(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long
    ): ArtifactList
}