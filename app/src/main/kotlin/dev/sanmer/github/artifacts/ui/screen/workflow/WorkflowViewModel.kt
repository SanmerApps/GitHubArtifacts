package dev.sanmer.github.artifacts.ui.screen.workflow

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.job.ArtifactJob
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.asLoadData
import dev.sanmer.github.artifacts.paging.WorkflowPagingSource
import dev.sanmer.github.artifacts.paging.WorkflowRunPagingSource
import dev.sanmer.github.artifacts.repository.ClientRepository
import dev.sanmer.github.query.workflow.run.WorkflowRunEvent
import dev.sanmer.github.query.workflow.run.WorkflowRunStatus
import dev.sanmer.github.response.artifact.Artifact
import dev.sanmer.github.response.workflow.Workflow
import dev.sanmer.github.response.workflow.run.WorkflowRun
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class WorkflowViewModel(
    private val clientRepository: ClientRepository,
    private val tokenId: Long,
    private val owner: String,
    val name: String,
) : ViewModel() {
    private val github = clientRepository.getOrDefault(tokenId)

    private val workflowsPager = WorkflowPagingSource(
        github = github,
        owner = owner,
        name = name
    ).asPager()
    val workflows = workflowsPager.flow.cachedIn(viewModelScope)

    private val queryFlow = MutableStateFlow(RunsQuery())
    val query = queryFlow.asStateFlow()
    val workflowRuns = queryFlow
        .flatMapLatest { (workflow, event, status) ->
            WorkflowRunPagingSource(
                github = github,
                owner = owner,
                name = name,
                workflowId = workflow?.id,
                event = event,
                status = status
            ).asPager().flow
        }.cachedIn(viewModelScope)

    private val artifacts = mutableStateMapOf<Long, LoadData<List<Artifact>>>()

    private val logger = Logger.Android("WorkflowViewModel")

    init {
        logger.d("init")
    }

    fun updateQuery(block: (RunsQuery) -> RunsQuery) {
        queryFlow.update(block)
    }

    fun artifacts(run: WorkflowRun) = artifacts.getOrDefault(run.id, LoadData.Pending)

    fun listArtifacts(run: WorkflowRun) {
        viewModelScope.launch {
            when (artifacts(run)) {
                LoadData.Pending, is LoadData.Failure -> {
                    artifacts[run.id] = LoadData.Loading
                    artifacts[run.id] = runCatching {
                        github.artifacts.list(
                            owner = owner,
                            repo = name,
                            runId = run.id
                        ).artifacts
                    }.asLoadData()
                }

                else -> {}
            }
        }
    }

    fun downloadArtifact(
        context: Context,
        artifact: Artifact
    ) = ArtifactJob.start(
        context = context,
        artifact = artifact,
        tokenId = tokenId
    )

    data class RunsQuery(
        val workflow: Workflow? = null,
        val event: WorkflowRunEvent? = null,
        val status: WorkflowRunStatus? = null
    )
}