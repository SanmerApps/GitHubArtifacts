package dev.sanmer.github.request.workflow.run

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WorkflowRunStatus {
    @SerialName("action_required")
    ActionRequired,

    @SerialName("cancelled")
    Cancelled,

    @SerialName("completed")
    Completed,

    @SerialName("failure")
    Failure,

    @SerialName("in_progress")
    InProgress,

    @SerialName("neutral")
    Neutral,

    @SerialName("pending")
    Pending,

    @SerialName("queued")
    Queued,

    @SerialName("requested")
    Requested,

    @SerialName("skipped")
    Skipped,

    @SerialName("stale")
    Stale,

    @SerialName("success")
    Success,

    @SerialName("timed_out")
    TimedOut,

    @SerialName("waiting")
    Waiting;

    override fun toString() = serializer().descriptor.getElementName(ordinal)
}