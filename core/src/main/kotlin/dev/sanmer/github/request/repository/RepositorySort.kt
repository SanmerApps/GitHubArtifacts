package dev.sanmer.github.request.repository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RepositorySort {
    @SerialName("created")
    Created,

    @SerialName("updated")
    Updated,

    @SerialName("pushed")
    Pushed,

    @SerialName("full_name")
    FullName;

    override fun toString() = serializer().descriptor.getElementName(ordinal)
}