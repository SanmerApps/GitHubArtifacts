package dev.sanmer.github.response.artifact

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

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
    @Contextual
    val createdAt: Instant,
    @SerialName("expires_at")
    @Contextual
    val expiresAt: Instant,
    @SerialName("updated_at")
    @Contextual
    val updatedAt: Instant,
    val digest: String = ""
)