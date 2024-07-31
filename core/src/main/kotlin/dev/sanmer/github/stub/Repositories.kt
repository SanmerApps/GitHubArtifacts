package dev.sanmer.github.stub

import dev.sanmer.github.response.Repository
import dev.sanmer.github.response.RepositoryList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Repositories {
    @Headers("Accept: application/vnd.github+json")
    @GET("users/{owner}/repos")
    suspend fun list(
        @Path("owner") owner: String,
        @Query("sort") sort: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): RepositoryList

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{name}")
    suspend fun get(
        @Path("owner") owner: String,
        @Path("name") name: String,
    ): Repository
}