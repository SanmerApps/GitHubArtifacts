package dev.sanmer.github.artifacts.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.RepoWithToken
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repo ORDER BY updatedAt DESC")
    fun getAllAsFlow(): Flow<List<RepoEntity>>

    @Transaction
    @Query("SELECT * FROM repo ORDER BY updatedAt DESC")
    fun getAllWithTokenAsFlow(): Flow<List<RepoWithToken>>

    @Query("SELECT * FROM repo WHERE id = :id")
    fun getAsFlow(id: Long): Flow<RepoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: RepoEntity)

    @Delete
    suspend fun delete(value: RepoEntity)
}