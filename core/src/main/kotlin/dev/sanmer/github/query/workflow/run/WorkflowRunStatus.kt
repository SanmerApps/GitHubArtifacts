package dev.sanmer.github.query.workflow.run

enum class WorkflowRunStatus(private val value: String) {
    ActionRequired("action_required"),
    Cancelled("cancelled"),
    Completed("completed"),
    Failure("failure"),
    InProgress("in_progress"),
    Neutral("neutral"),
    Pending("pending"),
    Queued("queued"),
    Requested("requested"),
    Skipped("skipped"),
    Stale("stale"),
    Success("success"),
    TimedOut("timed_out"),
    Waiting("waiting");

    override fun toString() = value
}