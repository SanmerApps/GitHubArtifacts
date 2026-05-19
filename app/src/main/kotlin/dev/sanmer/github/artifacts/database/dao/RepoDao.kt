package dev.sanmer.github.artifacts.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repo ORDER BY updatedAt DESC")
    fun getAllAsFlow(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repo ORDER BY updatedAt DESC")
    suspend fun getAll(): List<RepoEntity>

    @Transaction
    @Query("SELECT * FROM repo ORDER BY updatedAt DESC")
    fun getAllAndTokenAsFlow(): Flow<List<RepoEntity.AndToken>>

    @Transaction
    @Query("SELECT * FROM repo ORDER BY updatedAt DESC")
    suspend fun getAllAndToken(): List<RepoEntity.AndToken>

    @Query("SELECT * FROM repo WHERE id = :id")
    fun getAsFlow(id: Long): Flow<RepoEntity?>

    @Query("SELECT * FROM repo WHERE id = :id")
    suspend fun get(id: Long): RepoEntity

    @Transaction
    @Query("SELECT * FROM repo WHERE id = :id")
    fun getAndTokenAsFlow(id: Long): Flow<RepoEntity.AndToken?>

    @Transaction
    @Query("SELECT * FROM repo WHERE id = :id")
    suspend fun getAndToken(id: Long): RepoEntity.AndToken

    @Insert
    suspend fun insert(value: RepoEntity)

    @Update
    suspend fun update(value: RepoEntity)

    @Update
    suspend fun update(values: List<RepoEntity>)

    @Delete
    suspend fun delete(value: RepoEntity)

    @Query("DELETE FROM repo WHERE id = :id")
    suspend fun delete(id: Long)
}