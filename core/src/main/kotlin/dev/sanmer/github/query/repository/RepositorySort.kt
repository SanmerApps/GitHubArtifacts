package dev.sanmer.github.query.repository

enum class RepositorySort(private val value: String) {
    Created("created"),
    Updated("updated"),
    Pushed("pushed"),
    FullName("full_name");

    override fun toString(): String {
        return value
    }
}