package dev.sanmer.github

import dev.sanmer.github.serializer.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.serializersModuleOf

object JsonCompat {
    val default = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        serializersModule = serializersModuleOf(InstantSerializer)
    }

    val printer = Json(default) {
        prettyPrint = true
    }

    inline fun <reified T> String.decodeJson(): T =
        default.decodeFromString(this)

    inline fun <reified T> T.encodeJson(pretty: Boolean) = if (pretty) {
        printer.encodeToString(this)
    } else {
        default.encodeToString(this)
    }
}