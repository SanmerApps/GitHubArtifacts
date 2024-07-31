package dev.sanmer.github.artifacts.repository

import dev.sanmer.github.artifacts.database.dao.RepoDao
import dev.sanmer.github.artifacts.database.dao.TokenDao
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbRepository @Inject constructor(
    private val tokenDao: TokenDao,
    private val repoDao: RepoDao
) {
    val tokenAndRepoFlow get() = tokenDao.getAllWithRepoAsFlow()

    suspend fun getTokenAll() = withContext(Dispatchers.IO) {
        tokenDao.getAll()
    }

    fun getTokenAsFlow(token: String) = tokenDao.getAsFlow(token).filterNotNull()

    suspend fun insertToken(token: TokenEntity) = withContext(Dispatchers.IO) {
        tokenDao.insert(token)
    }

    suspend fun deleteToken(token: TokenEntity) = withContext(Dispatchers.IO) {
        tokenDao.delete(token)
    }

    val repoFlow get() = repoDao.getAllAsFlow()

    val repoAndTokenFlow get() = repoDao.getAllWithTokenAsFlow()

    fun getRepoAsFlow(id: Long) = repoDao.getAsFlow(id).filterNotNull()

    suspend fun insertRepo(repo: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.insert(repo)
    }

    suspend fun deleteRepo(repo: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.delete(repo)
    }
}