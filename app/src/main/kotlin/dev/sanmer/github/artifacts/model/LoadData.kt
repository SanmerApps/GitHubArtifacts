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

    companion object None : LoadData<Nothing>() {
        override val isCompleted = false

        fun <V> Result<V>.asLoadData(): LoadData<V> {
            return when {
                isSuccess -> Success(getOrThrow())
                else -> Failure(requireNotNull(exceptionOrNull()))
            }
        }

        inline fun <V> LoadData<V>.getValue(default: () -> V): V {
            return (this as? Success)?.value ?: default()
        }
    }
}