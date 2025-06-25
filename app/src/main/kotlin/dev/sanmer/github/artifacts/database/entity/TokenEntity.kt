package dev.sanmer.github.artifacts.database.entity

import androidx.room.Entity
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

@Entity(tableName = "token", primaryKeys = ["token"])
data class TokenEntity(
    val token: String,
    val name: String,
    val createdAt: Instant,
    val lifetime: Long
) {
    val expiredAt by lazy { createdAt + lifetime.days }
}