package dev.sanmer.github.request.workflow.run

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WorkflowRunEvent {
    @SerialName("branch_protection_rule")
    BranchProtectionRule,

    @SerialName("check_run")
    CheckRun,

    @SerialName("check_suite")
    CheckSuite,

    @SerialName("create")
    Create,

    @SerialName("delete")
    Delete,

    @SerialName("deployment")
    Deployment,

    @SerialName("deployment_status")
    DeploymentStatus,

    @SerialName("discussion")
    Discussion,

    @SerialName("discussion_comment")
    DiscussionComment,

    @SerialName("dynamic")
    Dynamic,

    @SerialName("fork")
    Fork,

    @SerialName("gollum")
    Gollum,

    @SerialName("image_version")
    ImageVersion,

    @SerialName("issue_comment")
    IssueComment,

    @SerialName("issues")
    Issues,

    @SerialName("label")
    Label,

    @SerialName("merge_group")
    MergeGroup,

    @SerialName("milestone")
    Milestone,

    @SerialName("page_build")
    PageBuild,

    @SerialName("public")
    Public,

    @SerialName("pull_request")
    PullRequest,

    @SerialName("pull_request_review")
    PullRequestReview,

    @SerialName("pull_request_review_comment")
    PullRequestReviewComment,

    @SerialName("pull_request_target")
    PullRequestTarget,

    @SerialName("push")
    Push,

    @SerialName("registry_package")
    RegistryPackage,

    @SerialName("release")
    Release,

    @SerialName("repository_dispatch")
    RepositoryDispatch,

    @SerialName("schedule")
    Schedule,

    @SerialName("status")
    Status,

    @SerialName("watch")
    Watch,

    @SerialName("workflow_call")
    WorkflowCall,

    @SerialName("workflow_dispatch")
    WorkflowDispatch,

    @SerialName("workflow_run")
    WorkflowRun;

    override fun toString() = serializer().descriptor.getElementName(ordinal)
}