package dev.sanmer.github.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Artifact(
    val id: Long,
    @JsonNames("node_id")
    val nodeId: String,
    val name: String,
    @JsonNames("size_in_bytes")
    val sizeInBytes: Long,
    val url: String,
    @JsonNames("archive_download_url")
    val archiveDownloadUrl: String,
    val expired: Boolean,
    @JsonNames("created_at")
    val createdAt: Instant,
    @JsonNames("expires_at")
    val expiresAt: Instant,
    @JsonNames("updated_at")
    val updatedAt: Instant,
    val digest: String = ""
)