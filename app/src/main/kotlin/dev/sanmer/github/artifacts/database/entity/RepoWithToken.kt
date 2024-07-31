package dev.sanmer.github.artifacts.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class RepoWithToken(
    @Embedded
    val repo: RepoEntity,
    @Relation(parentColumn = "token", entityColumn = "token")
    val token: TokenEntity
)