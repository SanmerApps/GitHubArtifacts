package dev.sanmer.github.artifacts.ktx

import java.util.Locale

fun Long.format() = when {
    this >= 1000000 -> String.format(
        locale = Locale.getDefault(),
        format = "%.1fM",
        this / 1000000.0
    )

    this >= 1000 -> String.format(
        locale = Locale.getDefault(),
        format = "%.1fk",
        this / 1000.0
    )

    else -> toString()
}

fun Int.format() = toLong().format()

fun Long.formatFileSize() = when {
    this >= 1073741824 -> String.format(
        locale = Locale.getDefault(),
        format = "%.2f GB",
        this / 1073741824.0
    )

    this >= 1048576 -> String.format(
        locale = Locale.getDefault(),
        format = "%.2f MB",
        this / 1048576.0
    )

    this >= 1024 -> String.format(
        locale = Locale.getDefault(),
        format = "%.2f KB",
        this / 1024.0
    )

    else -> "$this Bytes"
}

fun Int.formatFileSize() = toLong().formatFileSize()