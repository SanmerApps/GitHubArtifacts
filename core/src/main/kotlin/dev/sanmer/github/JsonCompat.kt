package dev.sanmer.github

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json

internal object JsonCompat : StringFormat {
    private val default = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override val serializersModule get() = default.serializersModule

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return default.decodeFromString(deserializer, string)
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        return default.encodeToString(serializer, value)
    }
}