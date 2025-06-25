package dev.sanmer.github.artifacts.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.Auth.Default.toBearerAuth
import dev.sanmer.github.GitHub
import dev.sanmer.github.artifacts.job.ArtifactJob
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.Default.asLoadData
import dev.sanmer.github.artifacts.repository.ClientRepository
import dev.sanmer.github.artifacts.ui.main.Screen
import dev.sanmer.github.query.workflow.run.WorkflowRunEvent
import dev.sanmer.github.query.workflow.run.WorkflowRunStatus
import dev.sanmer.github.response.artifact.Artifact
import dev.sanmer.github.response.workflow.run.WorkflowRun
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val workflow = savedStateHandle.toRoute<Screen.Workflow>()
    private val github by lazy { clientRepository.getOrNew(workflow.token.toBearerAuth()) }

    val name get() = workflow.name

    val workflowRuns by lazy {
        WorkflowRunPagingSource(
            github = github,
            owner = workflow.owner,
            name = workflow.name,
            perPage = 20
        ).asPager().flow.cachedIn(viewModelScope)
    }

    private val artifacts = mutableStateMapOf<Long, LoadData<List<Artifact>>>()

    init {
        Timber.d("WorkflowViewModel init")
    }

    fun getArtifacts(run: WorkflowRun): LoadData<List<Artifact>> {
        viewModelScope.launch {
            val data = artifacts.getOrDefault(
                run.id,
                LoadData.Failure(IllegalStateException("Padding"))
            )
            if (data is LoadData.Failure) {
                artifacts[run.id] = runCatching {
                    github.workflowRuns.getArtifacts(
                        owner = workflow.owner,
                        name = workflow.name,
                        runId = run.id
                    ).artifacts
                }.asLoadData()
            }
        }

        return artifacts.getOrDefault(run.id, LoadData.Loading)
    }

    fun downloadArtifact(context: Context, artifact: Artifact) {
        ArtifactJob.start(
            context = context,
            artifact = artifact,
            token = workflow.token
        )
    }

    data class WorkflowRunPagingSource(
        private val github: GitHub,
        private val owner: String,
        private val name: String,
        private val event: WorkflowRunEvent = WorkflowRunEvent.Push,
        private val status: WorkflowRunStatus = WorkflowRunStatus.Success,
        private val perPage: Int = 30
    ) : PagingSource<Int, WorkflowRun>() {
        override fun getRefreshKey(state: PagingState<Int, WorkflowRun>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WorkflowRun> {
            return try {
                val page = params.key ?: 1
                val workflowRuns = github.workflowRuns.list(
                    owner = owner,
                    name = name,
                    event = event,
                    status = status,
                    perPage = perPage,
                    page = page
                ).workflowRuns

                LoadResult.Page(
                    data = workflowRuns,
                    prevKey = if (page == 1) null else page.minus(1),
                    nextKey = if (workflowRuns.size != perPage) null else page.plus(1),
                )
            } catch (e: Exception) {
                Timber.e(e)
                LoadResult.Error(e)
            }
        }

        fun asPager() = Pager(
            config = PagingConfig(pageSize = perPage),
            pagingSourceFactory = ::copy
        )
    }
}