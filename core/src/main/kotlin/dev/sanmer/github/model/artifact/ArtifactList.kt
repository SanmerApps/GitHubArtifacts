package dev.sanmer.github.model.artifact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtifactList(
    @SerialName("total_count")
    val totalCount: Int,
    val artifacts: List<Artifact>
)