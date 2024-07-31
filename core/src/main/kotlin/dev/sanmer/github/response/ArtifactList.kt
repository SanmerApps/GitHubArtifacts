package dev.sanmer.github.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ArtifactList(
    @JsonNames("total_count")
    val totalCount: Int,
    val artifacts: List<Artifact>
)