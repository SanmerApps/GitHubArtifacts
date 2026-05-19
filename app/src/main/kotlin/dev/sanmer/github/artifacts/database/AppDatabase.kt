package dev.sanmer.github.artifacts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import dev.sanmer.github.artifacts.database.dao.RepoDao
import dev.sanmer.github.artifacts.database.dao.TokenDao
import dev.sanmer.github.artifacts.database.entity.RepoEntity
import dev.sanmer.github.artifacts.database.entity.TokenEntity
import kotlin.time.Instant

@Database(version = 3, entities = [TokenEntity::class, RepoEntity::class])
@TypeConverters(AppDatabase.Default::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun token(): TokenDao
    abstract fun repo(): RepoDao

    companion object Default {
        @TypeConverter
        fun stringToInstant(value: String) = Instant.parse(value)

        @TypeConverter
        fun instantToString(value: Instant) = value.toString()

        fun build(context: Context) =
            Room.databaseBuilder<AppDatabase>(
                context = context,
                name = "artifacts"
            ).addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3
            ).build()

        private val MIGRATION_1_2 = Migration(1, 2) {
            it.execSQL("CREATE TABLE IF NOT EXISTS token_new (token TEXT NOT NULL, name TEXT NOT NULL, createdAt TEXT NOT NULL, lifetime INTEGER NOT NULL, PRIMARY KEY(token))")
            it.execSQL("INSERT INTO token_new (token, name, createdAt, lifetime) SELECT token, name, updatedAt, 90 FROM token")
            it.execSQL("DROP TABLE token")
            it.execSQL("ALTER TABLE token_new RENAME TO token")
        }

        private val MIGRATION_2_3 = Migration(2, 3) {
            it.execSQL("CREATE TABLE IF NOT EXISTS token_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, token TEXT NOT NULL, name TEXT NOT NULL, createdAt TEXT NOT NULL, lifetime INTEGER NOT NULL)")
            it.execSQL("INSERT INTO token_new (token, name, createdAt, lifetime) SELECT token, name, createdAt, lifetime FROM token")
            it.execSQL("DROP TABLE token")
            it.execSQL("ALTER TABLE token_new RENAME TO token")

            it.execSQL("CREATE TABLE IF NOT EXISTS repo_new (id INTEGER NOT NULL, tokenId INTEGER NOT NULL, name TEXT NOT NULL, fullName TEXT NOT NULL, owner TEXT NOT NULL, private INTEGER NOT NULL, description TEXT NOT NULL, language TEXT NOT NULL, forksCount INTEGER NOT NULL, stargazersCount INTEGER NOT NULL, watchersCount INTEGER NOT NULL, openIssuesCount INTEGER NOT NULL, isTemplate INTEGER NOT NULL, hasIssues INTEGER NOT NULL, archived INTEGER NOT NULL, pushedAt TEXT NOT NULL, updatedAt TEXT NOT NULL, license TEXT NOT NULL, PRIMARY KEY(id))")
            it.execSQL(
                "INSERT INTO repo_new (id, tokenId, name, fullName, owner, private, description, language, forksCount, stargazersCount, watchersCount, openIssuesCount, isTemplate, hasIssues, archived, pushedAt, updatedAt, license) " +
                        "SELECT r.id, t.id, r.name, r.fullName, r.owner, r.private, r.description, r.language, r.forksCount, r.stargazersCount, r.watchersCount, r.openIssuesCount, r.isTemplate, r.hasIssues, r.archived, r.pushedAt, r.updatedAt, r.license " +
                        "FROM repo r INNER JOIN token t ON r.token = t.token"
            )
            it.execSQL("DROP TABLE repo")
            it.execSQL("ALTER TABLE repo_new RENAME TO repo")
        }
    }
}