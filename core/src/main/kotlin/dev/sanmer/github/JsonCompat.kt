package dev.sanmer.github

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json

object JsonCompat : StringFormat {
    val default = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val printer = Json(default) {
        prettyPrint = true
    }

    override val serializersModule = default.serializersModule

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return default.decodeFromString(deserializer, string)
    }

    inline fun <reified T> String.decodeJson(): T =
        default.decodeFromString(this)

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        return default.encodeToString(serializer, value)
    }

    inline fun <reified T> T.encodeJson(pretty: Boolean) = if (pretty) {
        printer.encodeToString(this)
    } else {
        default.encodeToString(this)
    }
}