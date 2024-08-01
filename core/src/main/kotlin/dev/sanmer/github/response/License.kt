package dev.sanmer.github.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class License(
    val key: String,
    val name: String,
    @JsonNames("spdx_id")
    val spdxId: String,
    val url: String = "",
    @JsonNames("node_id")
    val nodeId: String
) {
    val isEmpty inline get() = key.isBlank()

    companion object Empty {
        operator fun invoke() =
            License(
                key = "",
                name = "",
                spdxId = "",
                url = "",
                nodeId = ""
            )
    }
}
