package dev.sanmer.github.response.artifact

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artifact(
    val id: Long,
    @SerialName("node_id")
    val nodeId: String,
    val name: String,
    @SerialName("size_in_bytes")
    val sizeInBytes: Long,
    val url: String,
    @SerialName("archive_download_url")
    val archiveDownloadUrl: String,
    val expired: Boolean,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("expires_at")
    val expiresAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
    val digest: String = ""
)