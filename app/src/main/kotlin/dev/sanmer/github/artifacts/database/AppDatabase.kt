package dev.sanmer.github.artifacts.database

import android.content.Context
import androidx.room3.ColumnTypeConverter
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.execSQL
import dev.sanmer.github.artifacts.database.dao.RepoDao
import dev.sanmer.github.artifacts.database.dao.TokenDao
import dev.sanmer.github.artifacts.database.model.Repo
import dev.sanmer.github.artifacts.database.model.Token
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

@Database(
    entities = [
        Token::class,
        Repo::class
    ],
    version = 4
)
@ColumnTypeConverters(AppDatabase.Default::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun token(): TokenDao
    abstract fun repo(): RepoDao

    companion object Default {
        @ColumnTypeConverter
        fun fromEpochMilliseconds(value: Long) = Instant.fromEpochMilliseconds(value)

        @ColumnTypeConverter
        fun toEpochMilliseconds(value: Instant) = value.toEpochMilliseconds()

        fun build(context: Context) =
            Room.databaseBuilder<AppDatabase>(
                context = context,
                name = "artifacts"
            ).addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4
            ).build()

        private val MIGRATION_1_2 = Migration(1, 2) { connection ->
            connection.execSQL("CREATE TABLE token_new (token TEXT NOT NULL, name TEXT NOT NULL, createdAt TEXT NOT NULL, lifetime INTEGER NOT NULL, PRIMARY KEY(token))")
            connection.execSQL("INSERT INTO token_new (token, name, createdAt, lifetime) SELECT token, name, updatedAt, 365 FROM token")
            connection.execSQL("DROP TABLE token")
            connection.execSQL("ALTER TABLE token_new RENAME TO token")
        }

        private val MIGRATION_2_3 = Migration(2, 3) { connection ->
            connection.execSQL("CREATE TABLE token_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, token TEXT NOT NULL, name TEXT NOT NULL, expiredAt INTEGER NOT NULL)")
            connection.prepare("SELECT * FROM token").use { input ->
                val names = input.getColumnNames()
                val indexOfToken = names.indexOf("token")
                val indexOfName = names.indexOf("name")
                val indexOfCreatedAt = names.indexOf("createdAt")
                val indexOfLifetime = names.indexOf("lifetime")
                connection.prepare("INSERT INTO token_new (token, name, expiredAt) VALUES (?, ?, ?)")
                    .use { output ->
                        while (input.step()) {
                            output.bindText(1, input.getText(indexOfToken))
                            output.bindText(2, input.getText(indexOfName))
                            val createdAt = Instant.parse(input.getText(indexOfCreatedAt))
                            val lifetime = input.getLong(indexOfLifetime).days
                            val expiredAt = (createdAt + lifetime).toEpochMilliseconds()
                            output.bindLong(3, expiredAt)
                            output.step()
                            output.clearBindings()
                            output.reset()
                        }
                    }
            }
            connection.execSQL("DROP TABLE token")
            connection.execSQL("ALTER TABLE token_new RENAME TO token")

            connection.execSQL("CREATE TABLE repo_new (id INTEGER NOT NULL, tokenId INTEGER NOT NULL, name TEXT NOT NULL, fullName TEXT NOT NULL, owner TEXT NOT NULL, private INTEGER NOT NULL, description TEXT NOT NULL, language TEXT NOT NULL, forksCount INTEGER NOT NULL, stargazersCount INTEGER NOT NULL, watchersCount INTEGER NOT NULL, openIssuesCount INTEGER NOT NULL, isTemplate INTEGER NOT NULL, hasIssues INTEGER NOT NULL, archived INTEGER NOT NULL, pushedAt INTEGER NOT NULL, license TEXT NOT NULL, PRIMARY KEY(id))")
            connection.execSQL(
                "INSERT INTO repo_new (id, tokenId, name, fullName, owner, private, description, language, forksCount, stargazersCount, watchersCount, openIssuesCount, isTemplate, hasIssues, archived, pushedAt, license) " +
                        "SELECT r.id, t.id, r.name, r.fullName, r.owner, r.private, r.description, r.language, r.forksCount, r.stargazersCount, r.watchersCount, r.openIssuesCount, r.isTemplate, r.hasIssues, r.archived, 0, r.license " +
                        "FROM repo r INNER JOIN token t ON r.token = t.token"
            )
            connection.execSQL("DROP TABLE repo")
            connection.execSQL("ALTER TABLE repo_new RENAME TO repo")
        }

        private val MIGRATION_3_4 = Migration(3, 4) { connection ->
            connection.execSQL("CREATE TABLE Token_new (id INTEGER PRIMARY KEY NOT NULL, token TEXT NOT NULL, name TEXT NOT NULL, expiredAt INTEGER NOT NULL)")
            connection.execSQL("INSERT INTO Token_new (id, token, name, expiredAt) SELECT * FROM token")
            connection.execSQL("DROP TABLE token")
            connection.execSQL("ALTER TABLE Token_new RENAME TO Token")

            connection.execSQL("ALTER TABLE repo RENAME TO Repo_new")
            connection.execSQL("ALTER TABLE Repo_new RENAME TO Repo")
        }
    }
}