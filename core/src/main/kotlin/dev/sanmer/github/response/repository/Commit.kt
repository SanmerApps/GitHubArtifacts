package dev.sanmer.github.response.repository

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Commit(
    val id: String,
    @SerialName("tree_id")
    val treeId: String,
    val message: String,
    val timestamp: Instant,
    val author: Author = Author(),
    val committer: Author = Author()
)
