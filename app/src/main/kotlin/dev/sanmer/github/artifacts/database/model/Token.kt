package dev.sanmer.github.artifacts.database.model

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Relation
import kotlin.time.Instant

@Entity
data class Token(
    @PrimaryKey(
        autoGenerate = true,
        algorithm = PrimaryKey.Algorithm.ROWID
    )
    val id: Long = 0,
    val token: String,
    val name: String,
    val expiredAt: Instant
) {
    data class AndRepos(
        @Embedded
        val token: Token,
        @Relation(parentColumns = ["id"], entityColumns = ["tokenId"])
        val repos: List<Repo>
    )
}