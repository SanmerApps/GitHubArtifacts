package dev.sanmer.github.query.workflow.run

enum class WorkflowRunStatus(private val value: String) {
    Completed("completed"),
    ActionRequired("action_required"),
    Cancelled("cancelled"),
    Failure("failure"),
    Neutral("neutral"),
    Skipped("skipped"),
    Stale("stale"),
    Success("success"),
    TimedOut("timed_out"),
    InProgress("in_progress"),
    Queued("queued"),
    Requested("requested"),
    Waiting("waiting"),
    Pending("pending");

    override fun toString(): String {
        return value
    }
}