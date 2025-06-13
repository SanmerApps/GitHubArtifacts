package dev.sanmer.github.artifacts.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import dev.sanmer.github.artifacts.database.entity.TokenWithRepo
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM token ORDER BY name ASC")
    fun getAllAsFlow(): Flow<List<TokenEntity>>

    @Query("SELECT * FROM token ORDER BY name ASC")
    suspend fun getAll(): List<TokenEntity>

    @Transaction
    @Query("SELECT * FROM token ORDER BY name ASC")
    fun getAllWithRepoAsFlow(): Flow<List<TokenWithRepo>>

    @Query("SELECT * FROM token WHERE token = :token")
    fun getAsFlow(token: String): Flow<TokenEntity?>

    @Transaction
    @Query("SELECT * FROM token WHERE token = :token")
    suspend fun getWithRepo(token: String): TokenWithRepo

    @Insert
    suspend fun insert(value: TokenEntity)

    @Update
    suspend fun update(value: TokenEntity)

    @Delete
    suspend fun delete(value: TokenEntity)

    @Query("DELETE FROM token WHERE token = :token")
    suspend fun delete(token: String)
}