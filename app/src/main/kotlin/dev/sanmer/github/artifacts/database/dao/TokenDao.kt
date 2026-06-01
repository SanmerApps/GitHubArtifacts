package dev.sanmer.github.artifacts.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM token ORDER BY name ASC")
    fun getAllAsFlow(): Flow<List<TokenEntity>>

    @Query("SELECT * FROM token ORDER BY name ASC")
    suspend fun getAll(): List<TokenEntity>

    @Transaction
    @Query("SELECT * FROM token ORDER BY name ASC")
    fun getAllAndReposAsFlow(): Flow<List<TokenEntity.AndRepos>>

    @Transaction
    @Query("SELECT * FROM token ORDER BY name ASC")
    suspend fun getAllAndRepos(): List<TokenEntity.AndRepos>

    @Query("SELECT * FROM token WHERE id = :id")
    fun getAsFlow(id: Long): Flow<TokenEntity?>

    @Query("SELECT * FROM token WHERE id = :id")
    suspend fun get(id: Long): TokenEntity

    @Transaction
    @Query("SELECT * FROM token WHERE id = :id")
    fun getAndReposAsFlow(id: Long): Flow<TokenEntity.AndRepos?>

    @Transaction
    @Query("SELECT * FROM token WHERE id = :id")
    suspend fun getAndRepos(id: Long): TokenEntity.AndRepos

    @Insert
    suspend fun insert(value: TokenEntity)

    @Update
    suspend fun update(value: TokenEntity)

    @Delete
    suspend fun delete(value: TokenEntity)

    @Query("DELETE FROM token WHERE id = :id")
    suspend fun delete(id: Long)
}