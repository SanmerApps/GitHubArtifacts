package dev.sanmer.github.artifacts.ktx

import androidx.paging.LoadState

val LoadState.isLoading: Boolean
    inline get() = this is LoadState.Loading