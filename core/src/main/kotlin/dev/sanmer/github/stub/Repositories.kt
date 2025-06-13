package dev.sanmer.github.stub

import androidx.annotation.IntRange
import dev.sanmer.github.query.repository.RepositorySort
import dev.sanmer.github.response.repository.Repository
import dev.sanmer.github.response.repository.RepositoryList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Repositories {
    @Headers("Accept: application/vnd.github+json")
    @GET("users/{owner}/repos")
    suspend fun list(
        @Path("owner") owner: String,
        @Query("sort") sort: RepositorySort,
        @IntRange(1, 100) @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): RepositoryList

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{name}")
    suspend fun get(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): Repository
}