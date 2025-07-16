package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.artifacts.database.dao.RepoDao
import dev.sanmer.github.artifacts.database.dao.TokenDao
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

class DbRepositoryImpl(
    private val tokenDao: TokenDao,
    private val repoDao: RepoDao
) : DbRepository {
    override fun getTokensAsFlow() = tokenDao.getAllAsFlow()

    override fun getTokensWithReposAsFlow() = tokenDao.getAllWithRepoAsFlow()

    override fun getTokenAsFlow(token: String) = tokenDao.getAsFlow(token).filterNotNull()

    override suspend fun getTokenAll() = withContext(Dispatchers.IO) {
        tokenDao.getAll()
    }

    override suspend fun getTokenWithRepo(token: String) = withContext(Dispatchers.IO) {
        tokenDao.getWithRepo(token)
    }

    override suspend fun insertToken(token: TokenEntity) = withContext(Dispatchers.IO) {
        tokenDao.insert(token)
    }

    override suspend fun updateToken(token: TokenEntity) = withContext(Dispatchers.IO) {
        tokenDao.update(token)
    }

    override suspend fun deleteToken(token: TokenEntity) = withContext(Dispatchers.IO) {
        tokenDao.delete(token)
    }

    override suspend fun deleteToken(token: String) = withContext(Dispatchers.IO) {
        tokenDao.delete(token)
    }

    override fun getReposAsFlow() = repoDao.getAllAsFlow()

    override fun getReposWithTokenAsFlow() = repoDao.getAllWithTokenAsFlow()

    override fun getRepoAsFlow(id: Long) = repoDao.getAsFlow(id).filterNotNull()

    override suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        repoDao.getAll()
    }

    override suspend fun insertRepo(repo: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.insert(repo)
    }

    override suspend fun updateRepo(repo: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.update(repo)
    }

    override suspend fun updateRepo(repos: List<RepoEntity>) = withContext(Dispatchers.IO) {
        repoDao.update(repos)
    }

    override suspend fun deleteRepo(repo: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.delete(repo)
    }

    override suspend fun deleteRepo(repoId: Long) = withContext(Dispatchers.IO) {
        repoDao.deleteById(repoId)
    }
}