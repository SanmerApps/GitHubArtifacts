package dev.sanmer.github.artifacts.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.github.GitHubHandler
import dev.sanmer.github.GitHubHandler.Event
import dev.sanmer.github.GitHubHandler.Status
import dev.sanmer.github.artifacts.job.ArtifactJob
import dev.sanmer.github.artifacts.model.LoadData
import dev.sanmer.github.artifacts.model.LoadData.None.asLoadData
import dev.sanmer.github.artifacts.repository.DbRepository
import dev.sanmer.github.response.Artifact
import dev.sanmer.github.response.WorkflowRun
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id = savedStateHandle.id
    private var owner by mutableStateOf("")
    var name by mutableStateOf("")
        private set

    private var token = ""
    private val handler by lazy { GitHubHandler(token) }

    private val workflowRunsFlow = MutableStateFlow<LoadData<List<WorkflowRun>>>(LoadData.Loading)
    val workflowRuns get() = workflowRunsFlow.asStateFlow()

    private val artifacts = mutableStateMapOf<Long, LoadData<List<Artifact>>>()

    init {
        Timber.d("WorkflowViewModel init")
        repoObserver()
    }

    private fun repoObserver() {
        viewModelScope.launch {
            dbRepository.getRepoAsFlow(id)
                .collect { repo ->
                    token = repo.token
                    owner = repo.owner
                    name = repo.name

                    listWorkflows()
                }
        }
    }

    private fun listWorkflows() = with(handler) {
        viewModelScope.launch {
            workflowRunsFlow.update {
                runCatching {
                    listWorkflowRuns(
                        owner = owner,
                        name = name,
                        event = Event.Push,
                        status = Status.Success,
                        perPage = 30,
                        page = 1
                    )
                }.asLoadData()
            }
        }
    }

    fun updateWorkflows() {
        workflowRunsFlow.update { LoadData.Loading }
        listWorkflows()
    }

    fun getArtifacts(run: WorkflowRun) = with(handler) {
        viewModelScope.launch {
            artifacts.getOrPut(run.id) {
                runCatching {
                    getArtifacts(
                        owner = owner,
                        name = name,
                        runId = run.id
                    )
                }.asLoadData()
            }
        }

        artifacts.getOrDefault(run.id, LoadData.Loading)
    }

    fun downloadArtifact(context: Context, artifact: Artifact) {
        ArtifactJob.start(
            context = context,
            artifact = artifact,
            token = token
        )
    }

    companion object Util {
        private val SavedStateHandle.id: Long
            inline get() = checkNotNull(get("id"))
    }
}