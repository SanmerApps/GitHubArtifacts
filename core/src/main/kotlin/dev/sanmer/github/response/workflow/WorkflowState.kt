package dev.sanmer.github.response.workflow

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WorkflowState {
    @SerialName("active")
    Active,

    @SerialName("deleted")
    Deleted,

    @SerialName("disabled_fork")
    DisabledFork,

    @SerialName("disabled_inactivity")
    DisabledInactivity,

    @SerialName("disabled_manually")
    DisabledManually
}