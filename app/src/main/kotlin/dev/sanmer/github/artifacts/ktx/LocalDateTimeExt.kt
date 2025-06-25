@file:Suppress("NOTHING_TO_INLINE")

package dev.sanmer.github.artifacts.ktx

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

inline fun Long.toLocalDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
) = Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)

inline fun String.toLocalDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
) = Instant.parse(this).toLocalDateTime(timeZone)

inline fun Instant.toLocalDate(timeZone: TimeZone) = toLocalDateTime(timeZone).date

inline fun LocalDateTime.copy(
    year: Int = this.year,
    month: Int = this.month.number,
    day: Int = this.day,
    hour: Int = this.hour,
    minute: Int = this.minute,
    second: Int = this.second,
    nanosecond: Int = this.nanosecond
) = LocalDateTime(year, month, day, hour, minute, second, nanosecond)