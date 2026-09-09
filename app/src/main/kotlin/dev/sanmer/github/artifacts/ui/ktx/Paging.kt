package dev.sanmer.github.artifacts.ui.ktx

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

val LoadState.isLoading
    inline get() = this is LoadState.Loading

val CombinedLoadStates.isLoading
    inline get() = refresh.isLoading || append.isLoading || prepend.isLoading

fun <T : Any> LazyPagingItems<T>.isEmpty() = itemCount == 0

fun <T : Any> LazyPagingItems<T>.isNotEmpty() = itemCount != 0