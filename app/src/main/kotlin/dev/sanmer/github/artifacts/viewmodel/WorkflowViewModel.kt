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
import dev.sanmer.github.artifacts.model.Data
import dev.sanmer.github.artifacts.model.Data.None.data
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
    var owner by mutableStateOf("")
        private set
    var name by mutableStateOf("")
        private set

    private var token = ""
    private val handler by lazy { GitHubHandler(token) }

    private val workflowRunsFlow = MutableStateFlow<Data<List<WorkflowRun>>>(Data.Loading)
    val workflowRuns get() = workflowRunsFlow.asStateFlow()

    private val artifacts = mutableStateMapOf<Long, Data<List<Artifact>>>()

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
                }.data()
            }
        }
    }

    fun updateWorkflows() {
        workflowRunsFlow.update { Data.Loading }
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
                }.data()
            }
        }

        artifacts.getOrDefault(run.id, Data.Loading)
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