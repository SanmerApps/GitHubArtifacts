@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.github.artifacts.ktx

import java.util.Locale

inline fun Long.format() = when {
    this >= 1_000_000 -> String.format(
        locale = Locale.getDefault(),
        format = "%.1fm",
        this / 1_000_000.0
    )

    this >= 1_000 -> String.format(
        locale = Locale.getDefault(),
        format = "%.1fk",
        this / 1_000.0
    )

    else -> toString()
}

inline fun Int.format() = toLong().format()

inline fun Long.shortId(multiple: Long = 10L): Int {
    var value = this
    while (value > Int.MAX_VALUE || value < Int.MIN_VALUE) {
        value /= multiple
    }
    return value.toInt()
}