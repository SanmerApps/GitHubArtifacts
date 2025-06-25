package dev.sanmer.github.serializer

import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.serializers.FormattedInstantSerializer

object InstantSerializer : FormattedInstantSerializer(
    name = "ISO_DATE_TIME_OFFSET",
    format = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET
)