package dev.sanmer.github.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Commit(
    val id: String,
    @JsonNames("tree_id")
    val treeId: String,
    val message: String,
    val timestamp: Instant,
    val author: Author = Author.EMPTY,
    val committer: Author = Author.EMPTY
)
