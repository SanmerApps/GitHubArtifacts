package dev.sanmer.github

import androidx.annotation.IntRange
import dev.sanmer.github.response.Owner
import dev.sanmer.github.response.Repository
import dev.sanmer.github.response.WorkflowRun
import dev.sanmer.github.stub.Artifacts
import dev.sanmer.github.stub.Repositories
import dev.sanmer.github.stub.WorkflowRuns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitHubHandler private constructor(
    token: String
) : HttpHandler(
    baseUrl = BASE_URL,
    auth = Auth.Bearer(token)
) {
    private val repositories by lazy { create<Repositories>() }
    private val workflowRuns by lazy { create<WorkflowRuns>() }
    private val artifacts by lazy { create<Artifacts>() }

    init {
        addHeader("X-GitHub-Api-Version", API_VERSION)
        addHeader("User-Agent", "GithubHandler/$API_VERSION")
    }

    suspend fun listRepo(
        owner: String,
        sort: Sort,
        @IntRange(1, 100) perPage: Int,
        page: Int
    ) = withContext(Dispatchers.IO) {
        repositories.list(
            owner = owner,
            sort = sort.value,
            perPage = perPage,
            page = page
        ).repositories
    }

    suspend fun Owner.listRepo(
        sort: Sort = Sort.Pushed,
        @IntRange(1, 100) perPage: Int = 30,
        page: Int = 1
    ) = listRepo(
        owner = login,
        sort = sort,
        perPage = perPage,
        page = page
    )

    suspend fun getRepo(
        owner: String,
        name: String
    ) = withContext(Dispatchers.IO) {
        repositories.get(
            owner = owner,
            name = name
        )
    }

    suspend fun Owner.getRepo(
        name: String
    ) = getRepo(
        owner = login,
        name = name
    )

    suspend fun listWorkflowRuns(
        owner: String,
        name: String,
        event: Event,
        status: Status,
        @IntRange(1, 100) perPage: Int,
        page: Int
    ) = withContext(Dispatchers.IO) {
        workflowRuns.list(
            owner = owner,
            name = name,
            event = event.value,
            status = status.value,
            perPage = perPage,
            page = page
        ).workflowRuns
    }

    suspend fun Repository.listWorkflowRuns(
        event: Event = Event.Push,
        status: Status = Status.Success,
        @IntRange(1, 100) perPage: Int = 30,
        page: Int = 1
    ) = listWorkflowRuns(
        owner = owner.login,
        name = name,
        event = event,
        status = status,
        perPage = perPage,
        page = page
    )

    suspend fun getArtifacts(
        owner: String,
        name: String,
        runId: Long
    ) = withContext(Dispatchers.IO) {
        workflowRuns.getArtifacts(
            owner = owner,
            name = name,
            runId = runId
        ).artifacts
    }

    suspend fun Repository.getArtifacts(
        run: WorkflowRun
    ) = getArtifacts(
        owner = owner.login,
        name = name,
        runId = run.id
    )

    suspend fun listArtifacts(
        owner: String,
        name: String,
        @IntRange(1, 100) perPage: Int,
        page: Int
    ) = withContext(Dispatchers.IO) {
        artifacts.list(
            owner = owner,
            name = name,
            perPage = perPage,
            page = page
        ).artifacts
    }

    suspend fun Repository.listArtifacts(
        @IntRange(1, 100) perPage: Int = 30,
        page: Int = 1
    ) = listArtifacts(
        owner = owner.login,
        name = name,
        perPage = perPage,
        page = page
    )

    enum class Sort(internal val value: String) {
        Created("created"),
        Updated("updated"),
        Pushed("pushed"),
        FullName("full_name")
    }

    enum class Status(internal val value: String) {
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
        Pending("pending")
    }

    enum class Event(internal val value: String) {
        PullRequest("pull_request"),
        Push("push"),
        Release("release"),
        Schedule("schedule"),
        WorkflowDispatch("workflow_dispatch")
    }

    companion object Default {
        const val API_VERSION = "2022-11-28"
        const val BASE_URL = "https://api.github.com/"

        private val handlers = hashMapOf<String, GitHubHandler>()
        operator fun invoke(token: String) = handlers.getOrPut(token) { GitHubHandler(token) }
    }
}