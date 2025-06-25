package dev.sanmer.github.artifacts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanmer.github.artifacts.database.dao.RepoDao
import dev.sanmer.github.artifacts.database.dao.TokenDao
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import javax.inject.Singleton
import kotlin.time.Instant

@Database(version = 2, entities = [TokenEntity::class, RepoEntity::class])
@TypeConverters(AppDatabase.Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun token(): TokenDao
    abstract fun repo(): RepoDao

    companion object Build {
        operator fun invoke(context: Context) =
            Room.databaseBuilder(
                context, AppDatabase::class.java, "artifacts"
            ).addMigrations(
                MIGRATION_1_2
            ).build()

        private val MIGRATION_1_2 = Migration(1, 2) {
            it.execSQL("CREATE TABLE IF NOT EXISTS token_new (token TEXT NOT NULL, name TEXT NOT NULL, createdAt TEXT NOT NULL, lifetime INTEGER NOT NULL, PRIMARY KEY(token))")
            it.execSQL("INSERT INTO token_new (token, name, createdAt, lifetime) SELECT token, name, updatedAt, 90 FROM token")
            it.execSQL("DROP TABLE token")
            it.execSQL("ALTER TABLE token_new RENAME TO token")
        }
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
    object Impl {
        @Provides
        @Singleton
        fun AppDatabase(
            @ApplicationContext context: Context
        ) = Build(context)

        @Provides
        @Singleton
        fun TokenDao(db: AppDatabase) = db.token()

        @Provides
        @Singleton
        fun RepoDao(db: AppDatabase) = db.repo()
    }
}