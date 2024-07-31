package dev.sanmer.github.artifacts.model

sealed class Data<out V> {
    abstract val isCompleted: Boolean

    data object Loading : Data<Nothing>() {
        override val isCompleted = false
    }

    data class Success<out V>(val value: V) : Data<V>() {
        override val isCompleted = true
    }

    data class Failure(val error: Throwable) : Data<Nothing>() {
        override val isCompleted = true
    }

    companion object None : Data<Nothing>() {
        override val isCompleted = false

        fun <V> Result<V>.data(): Data<V> {
            return when {
                isSuccess -> Success(getOrThrow())
                else -> Failure(requireNotNull(exceptionOrNull()))
            }
        }
    }
}