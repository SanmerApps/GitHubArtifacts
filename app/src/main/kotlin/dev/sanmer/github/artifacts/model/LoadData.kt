package dev.sanmer.github.artifacts.model

sealed class LoadData<out V> {
    abstract val isCompleted: Boolean

    data object Loading : LoadData<Nothing>() {
        override val isCompleted = false
    }

    data class Success<out V>(val value: V) : LoadData<V>() {
        override val isCompleted = true
    }

    data class Failure(val error: Throwable) : LoadData<Nothing>() {
        override val isCompleted = true
    }

    companion object Default {
        fun <V> Result<V>.asLoadData(): LoadData<V> {
            return when {
                isSuccess -> Success(getOrThrow())
                else -> Failure(requireNotNull(exceptionOrNull()))
            }
        }
        fun <V> LoadData<V>.getOrThrow(): V {
            return when (this) {
                is Failure -> throw error
                Loading -> throw IllegalStateException("Loading")
                is Success<V> -> value
            }
        }

        inline fun <V, R> LoadData<V>.getValue(fallback: R, transform: (V) -> R): R {
            return (this as? Success)?.value?.let(transform) ?: fallback
        }
    }
}