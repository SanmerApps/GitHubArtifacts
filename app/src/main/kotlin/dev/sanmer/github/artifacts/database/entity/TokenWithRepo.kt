package dev.sanmer.github.artifacts.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TokenWithRepo(
    @Embedded
    val token: TokenEntity,
    @Relation(parentColumn = "token", entityColumn = "token")
    val repo: List<RepoEntity>
)