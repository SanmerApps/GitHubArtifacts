package dev.sanmer.github.artifacts.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.sanmer.github.GitHub
import dev.sanmer.github.GitHub.Default.toBearerAuth
import dev.sanmer.github.request.workflow.run.WorkflowRunEvent
import dev.sanmer.github.request.workflow.run.WorkflowRunStatus
import dev.sanmer.github.response.workflow.run.WorkflowRun

data class WorkflowRunPagingSource(
    private val github: GitHub,
    private val token: String,
    private val owner: String,
    private val name: String,
    private val perPage: Int = 20,
    private val workflowId: Long? = null,
    private val event: WorkflowRunEvent? = null,
    private val status: WorkflowRunStatus? = null,
    private val onProgress: (Float) -> Unit = {}
) : PagingSource<Int, WorkflowRun>() {
    override suspend fun load(params: LoadParams<Int>) = try {
        val page = params.key ?: 1
        val list = if (workflowId != null) {
            github.listWorkflowRun(
                auth = token.toBearerAuth(),
                owner = owner,
                repo = name,
                workflowId = workflowId,
                perPage = perPage,
                page = page,
                event = event,
                status = status
            )
        } else {
            github.listWorkflowRun(
                auth = token.toBearerAuth(),
                owner = owner,
                repo = name,
                perPage = perPage,
                page = page,
                event = event,
                status = status
            )
        }

        LoadResult.Page(
            data = list.workflowRuns,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (list.workflowRuns.size < perPage) null else page + 1,
            itemsBefore = (page - 1) * perPage,
            itemsAfter = (list.totalCount - page * perPage).coerceAtLeast(0)
        )
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, WorkflowRun>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    fun asPager() = Pager(
        config = PagingConfig(
            pageSize = perPage
        ),
        pagingSourceFactory = ::copy
    )
}