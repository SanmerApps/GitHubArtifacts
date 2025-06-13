package dev.sanmer.github.response.repository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    val login: String,
    val id: Long,
    @SerialName("node_id")
    val nodeId: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("html_url")
    val htmlUrl: String
)
