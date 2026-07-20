package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.database.model.Token
import kotlinx.coroutines.flow.Flow

interface DbRepository {
    fun getTokensAsFlow(): Flow<List<Token>>
    suspend fun getTokens(): List<Token>
    fun getTokensAndReposAsFlow(): Flow<List<Token.AndRepos>>
    suspend fun getTokensAndRepos(): List<Token.AndRepos>
    fun getTokenAsFlow(id: Long): Flow<Token>
    suspend fun getToken(id: Long): Token
    fun getTokenAndReposAsFlow(id: Long): Flow<Token.AndRepos>
    suspend fun getTokenAndRepos(id: Long): Token.AndRepos
    suspend fun upsertToken(vararg token: Token)
    suspend fun deleteToken(id: Long)

    fun getReposAsFlow(): Flow<List<Repo>>
    suspend fun getRepos(): List<Repo>
    fun getReposAndTokenAsFlow(): Flow<List<Repo.AndToken>>
    suspend fun getReposAndToken(): List<Repo.AndToken>
    fun getRepoAsFlow(id: Long): Flow<Repo>
    suspend fun getRepo(id: Long): Repo
    fun getRepoAndTokenAsFlow(id: Long): Flow<Repo.AndToken>
    suspend fun getRepoAndToken(id: Long): Repo.AndToken
    suspend fun upsertRepo(vararg repo: Repo)
    suspend fun deleteRepo(id: Long)
}