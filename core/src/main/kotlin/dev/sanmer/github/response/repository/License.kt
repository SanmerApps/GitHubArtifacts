package dev.sanmer.github.response.repository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class License(
    val key: String,
    val name: String,
    @SerialName("spdx_id")
    val spdxId: String,
    val url: String = "",
    @SerialName("node_id")
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
