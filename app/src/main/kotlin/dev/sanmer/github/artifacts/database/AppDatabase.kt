package dev.sanmer.github.artifacts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanmer.github.artifacts.database.dao.RepoDao
import dev.sanmer.github.artifacts.database.dao.TokenDao
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import kotlinx.datetime.Instant
import javax.inject.Singleton

@Database(version = 1, entities = [TokenEntity::class, RepoEntity::class])
@TypeConverters(AppDatabase.Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun token(): TokenDao
    abstract fun repo(): RepoDao

    companion object Default {
        fun build(context: Context) =
            Room.databaseBuilder(
                context, AppDatabase::class.java, "artifacts"
            ).build()
    }

    @Suppress("FunctionName")
    object Converter {
        @TypeConverter
        fun StringToInstant(value: String) = Instant.parse(value)

        @TypeConverter
        fun InstantToString(value: Instant) = value.toString()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object Provider {
        @Provides
        @Singleton
        fun AppDatabase(
            @ApplicationContext context: Context
        ) = build(context)

        @Provides
        @Singleton
        fun TokenDao(db: AppDatabase) = db.token()

        @Provides
        @Singleton
        fun RepoDao(db: AppDatabase) = db.repo()
    }
}