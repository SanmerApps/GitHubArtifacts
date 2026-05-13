package dev.sanmer.github.artifacts.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

@Entity(tableName = "token")
data class TokenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val token: String,
    val name: String,
    val createdAt: Instant,
    val lifetime: Long
) {
    val expiredAt by lazy { createdAt + lifetime.days }

    data class AndRepos(
        @Embedded
        val token: TokenEntity,
        @Relation(parentColumn = "id", entityColumn = "tokenId")
        val repos: List<RepoEntity>
    )
}