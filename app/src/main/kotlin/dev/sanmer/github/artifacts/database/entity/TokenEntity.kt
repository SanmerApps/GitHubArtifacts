package dev.sanmer.github.artifacts.database.entity

import androidx.room.Entity
import kotlinx.datetime.Instant

@Entity(tableName = "token", primaryKeys = ["token"])
data class TokenEntity(
    val token: String,
    val name: String,
    val updatedAt: Instant
)