package dev.sanmer.github.response.repository

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Commit(
    val id: String,
    @SerialName("tree_id")
    val treeId: String,
    val message: String,
    @Contextual
    val timestamp: Instant,
    val author: Author = Author(),
    val committer: Author = Author()
)
