package dev.sanmer.github.artifacts

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char

object Const {
    const val GITHUB_URL = "https://github.com/SanmerApps/GitHubArtifacts"

    const val CHANNEL_ID_ARTIFACT_JOB = "ARTIFACT_JOB"

    val DATETIME_DISPLAY = LocalDateTime.Format {
        date(LocalDate.Formats.ISO)
        char(' ')
        hour(); char(':'); minute(); char(':'); second()
    }
}