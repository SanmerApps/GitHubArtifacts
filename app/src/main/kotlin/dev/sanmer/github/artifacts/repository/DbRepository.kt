package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import kotlinx.coroutines.flow.Flow

interface DbRepository {
    fun getTokensAsFlow(): Flow<List<TokenEntity>>
    suspend fun getTokens(): List<TokenEntity>
    fun getTokensAndReposAsFlow(): Flow<List<TokenEntity.AndRepos>>
    suspend fun getTokensAndRepos(): List<TokenEntity.AndRepos>
    fun getTokenAsFlow(id: Long): Flow<TokenEntity>
    suspend fun getToken(id: Long): TokenEntity
    fun getTokenAndReposAsFlow(id: Long): Flow<TokenEntity.AndRepos>
    suspend fun getTokenAndRepos(id: Long): TokenEntity.AndRepos
    suspend fun insertToken(token: TokenEntity)
    suspend fun updateToken(token: TokenEntity)
    suspend fun deleteToken(token: TokenEntity)
    suspend fun deleteToken(id: Long)

    fun getReposAsFlow(): Flow<List<RepoEntity>>
    suspend fun getRepos(): List<RepoEntity>
    fun getReposAndTokenAsFlow(): Flow<List<RepoEntity.AndToken>>
    suspend fun getReposAndToken(): List<RepoEntity.AndToken>
    fun getRepoAsFlow(id: Long): Flow<RepoEntity>
    suspend fun getRepo(id: Long): RepoEntity
    fun getRepoAndTokenAsFlow(id: Long): Flow<RepoEntity.AndToken>
    suspend fun getRepoAndToken(id: Long): RepoEntity.AndToken
    suspend fun insertRepo(repo: RepoEntity)
    suspend fun updateRepo(repo: RepoEntity)
    suspend fun updateRepo(repos: List<RepoEntity>)
    suspend fun deleteRepo(repo: RepoEntity)
    suspend fun deleteRepo(id: Long)
}