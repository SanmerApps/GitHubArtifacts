package dev.sanmer.github.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Owner(
    val login: String,
    val id: Long,
    @JsonNames("node_id")
    val nodeId: String,
    @JsonNames("avatar_url")
    val avatarUrl: String,
    @JsonNames("html_url")
    val htmlUrl: String
)
