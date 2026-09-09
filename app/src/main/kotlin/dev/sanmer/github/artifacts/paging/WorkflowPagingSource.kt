package dev.sanmer.github.artifacts.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.sanmer.github.GitHub
import dev.sanmer.github.GitHub.Default.toBearerAuth
import dev.sanmer.github.response.workflow.Workflow

data class WorkflowPagingSource(
    private val github: GitHub,
    private val token: String,
    private val owner: String,
    private val name: String,
    private val perPage: Int = 20,
) : PagingSource<Int, Workflow>() {
    override suspend fun load(params: LoadParams<Int>) = try {
        val page = params.key ?: 1
        val list = github.listWorkflow(
            auth = token.toBearerAuth(),
            owner = owner,
            repo = name,
            perPage = perPage,
            page = page
        )

        LoadResult.Page(
            data = list.workflows,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (list.workflows.size < perPage) null else page + 1,
            itemsBefore = (page - 1) * perPage,
            itemsAfter = (list.totalCount - page * perPage).coerceAtLeast(0)
        )
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Workflow>) =
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