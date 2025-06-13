package dev.sanmer.github.query.workflow.run

enum class WorkflowRunEvent(private val value: String) {
    PullRequest("pull_request"),
    Push("push"),
    Release("release"),
    Schedule("schedule"),
    WorkflowDispatch("workflow_dispatch");

    override fun toString(): String {
        return value
    }
}