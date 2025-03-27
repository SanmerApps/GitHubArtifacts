package dev.sanmer.github.artifacts.ui.ktx

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

val LoadState.isError
    inline get() = this is LoadState.Error

val LoadState.isLoading
    inline get() = this is LoadState.Loading

val CombinedLoadStates.isLoading
    inline get() = refresh.isLoading || append.isLoading || prepend.isLoading

fun <T : Any> LazyPagingItems<T>.isEmpty() = itemCount == 0

fun <T : Any> LazyPagingItems<T>.isNotEmpty() = itemCount != 0

inline fun <T : Any> LazyListScope.items(
    items: LazyPagingItems<T>,
    noinline key: ((T) -> Any)? = null,
    noinline contentType: (T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) = items(
    count = items.itemCount,
    key = items.itemKey(key),
    contentType = items.itemContentType(contentType)
) {
    itemContent(items[it]!!)
}