package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.artifacts.database.dao.RepoDao
import dev.sanmer.github.artifacts.database.dao.TokenDao
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.database.model.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

class DbRepositoryImpl(
    private val tokenDao: TokenDao,
    private val repoDao: RepoDao
) : DbRepository {
    override fun getTokensAsFlow() = tokenDao.getAllAsFlow()

    override suspend fun getTokens() = withContext(Dispatchers.IO) {
        tokenDao.getAll()
    }

    override fun getTokensAndReposAsFlow() = tokenDao.getAllAndReposAsFlow()

    override suspend fun getTokensAndRepos() = withContext(Dispatchers.IO) {
        tokenDao.getAllAndRepos()
    }

    override fun getTokenAsFlow(id: Long) = tokenDao.getAsFlow(id).filterNotNull()

    override suspend fun getToken(id: Long) = withContext(Dispatchers.IO) {
        tokenDao.get(id)
    }

    override fun getTokenAndReposAsFlow(id: Long) = tokenDao.getAndReposAsFlow(id).filterNotNull()

    override suspend fun getTokenAndRepos(id: Long) = withContext(Dispatchers.IO) {
        tokenDao.getAndRepos(id)
    }

    override suspend fun upsertToken(vararg token: Token) = withContext(Dispatchers.IO) {
        tokenDao.upsert(*token)
    }

    override suspend fun deleteToken(id: Long) = withContext(Dispatchers.IO) {
        tokenDao.delete(id)
    }

    override fun getReposAsFlow() = repoDao.getAllAsFlow()

    override suspend fun getRepos() = withContext(Dispatchers.IO) {
        repoDao.getAll()
    }

    override fun getReposAndTokenAsFlow() = repoDao.getAllAndTokenAsFlow()

    override suspend fun getReposAndToken() = withContext(Dispatchers.IO) {
        repoDao.getAllAndToken()
    }

    override fun getRepoAsFlow(id: Long) = repoDao.getAsFlow(id).filterNotNull()

    override suspend fun getRepo(id: Long) = withContext(Dispatchers.IO) {
        repoDao.get(id)
    }

    override fun getRepoAndTokenAsFlow(id: Long) = repoDao.getAndTokenAsFlow(id).filterNotNull()

    override suspend fun getRepoAndToken(id: Long) = withContext(Dispatchers.IO) {
        repoDao.getAndToken(id)
    }

    override suspend fun upsertRepo(vararg repo: Repo) = withContext(Dispatchers.IO) {
        repoDao.upsert(*repo)
    }

    override suspend fun deleteRepo(id: Long) = withContext(Dispatchers.IO) {
        repoDao.delete(id)
    }
}