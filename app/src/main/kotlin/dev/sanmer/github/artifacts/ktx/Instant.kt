package dev.sanmer.github.artifacts.ktx

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Instant.toLocalDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
) = toLocalDateTime(timeZone).date

fun LocalDate.toInstant(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    time: LocalTime = LocalTime(0, 0, 0, 0)
) = LocalDateTime(this, time).toInstant(timeZone)