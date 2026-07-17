package dev.sanmer.github.artifacts.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import dev.sanmer.github.artifacts.database.model.Token
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM Token")
    fun getAllAsFlow(): Flow<List<Token>>

    @Query("SELECT * FROM Token")
    suspend fun getAll(): List<Token>

    @Transaction
    @Query("SELECT * FROM Token")
    fun getAllAndReposAsFlow(): Flow<List<Token.AndRepos>>

    @Transaction
    @Query("SELECT * FROM Token")
    suspend fun getAllAndRepos(): List<Token.AndRepos>

    @Query("SELECT * FROM Token WHERE id = :id")
    fun getAsFlow(id: Long): Flow<Token?>

    @Query("SELECT * FROM Token WHERE id = :id")
    suspend fun get(id: Long): Token

    @Transaction
    @Query("SELECT * FROM Token WHERE id = :id")
    fun getAndReposAsFlow(id: Long): Flow<Token.AndRepos?>

    @Transaction
    @Query("SELECT * FROM Token WHERE id = :id")
    suspend fun getAndRepos(id: Long): Token.AndRepos

    @Upsert
    suspend fun upsert(vararg token: Token)

    @Query("DELETE FROM Token WHERE id = :id")
    suspend fun delete(id: Long)
}