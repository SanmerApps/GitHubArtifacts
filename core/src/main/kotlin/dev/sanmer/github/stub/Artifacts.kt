package dev.sanmer.github.stub

import dev.sanmer.github.response.ArtifactList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Artifacts {
    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{name}/actions/artifacts")
    suspend fun list(
        @Path("owner") owner: String,
        @Path("name") name: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): ArtifactList
}