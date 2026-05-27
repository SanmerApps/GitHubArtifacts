package dev.sanmer.github.stub

import androidx.annotation.IntRange
import dev.sanmer.github.response.workflow.WorkflowList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Workflows {
    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/actions/workflows")
    suspend fun list(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): WorkflowList
}