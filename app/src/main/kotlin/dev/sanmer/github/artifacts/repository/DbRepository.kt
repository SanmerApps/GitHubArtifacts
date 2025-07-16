package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.RepoWithToken
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.database.entity.TokenWithRepo
import kotlinx.coroutines.flow.Flow

interface DbRepository {
    fun getTokensAsFlow(): Flow<List<TokenEntity>>
    fun getTokensWithReposAsFlow(): Flow<List<TokenWithRepo>>
    fun getTokenAsFlow(token: String): Flow<TokenEntity>
    suspend fun getTokenAll(): List<TokenEntity>
    suspend fun getTokenWithRepo(token: String): TokenWithRepo
    suspend fun insertToken(token: TokenEntity)
    suspend fun updateToken(token: TokenEntity)
    suspend fun deleteToken(token: TokenEntity)
    suspend fun deleteToken(token: String)

    fun getReposAsFlow(): Flow<List<RepoEntity>>
    fun getReposWithTokenAsFlow(): Flow<List<RepoWithToken>>
    fun getRepoAsFlow(id: Long): Flow<RepoEntity>
    suspend fun getRepoAll(): List<RepoEntity>
    suspend fun insertRepo(repo: RepoEntity)
    suspend fun updateRepo(repo: RepoEntity)
    suspend fun updateRepo(repos: List<RepoEntity>)
    suspend fun deleteRepo(repo: RepoEntity)
    suspend fun deleteRepo(repoId: Long)
}