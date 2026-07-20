package dev.sanmer.github.artifacts.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import dev.sanmer.github.artifacts.database.model.Repo
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM Repo")
    fun getAllAsFlow(): Flow<List<Repo>>

    @Query("SELECT * FROM Repo")
    suspend fun getAll(): List<Repo>

    @Transaction
    @Query("SELECT * FROM Repo")
    fun getAllAndTokenAsFlow(): Flow<List<Repo.AndToken>>

    @Transaction
    @Query("SELECT * FROM Repo")
    suspend fun getAllAndToken(): List<Repo.AndToken>

    @Query("SELECT * FROM Repo WHERE id = :id")
    fun getAsFlow(id: Long): Flow<Repo?>

    @Query("SELECT * FROM Repo WHERE id = :id")
    suspend fun get(id: Long): Repo

    @Transaction
    @Query("SELECT * FROM Repo WHERE id = :id")
    fun getAndTokenAsFlow(id: Long): Flow<Repo.AndToken?>

    @Transaction
    @Query("SELECT * FROM Repo WHERE id = :id")
    suspend fun getAndToken(id: Long): Repo.AndToken

    @Upsert
    suspend fun upsert(vararg repo: Repo)

    @Query("DELETE FROM Repo WHERE id = :id")
    suspend fun delete(id: Long)
}