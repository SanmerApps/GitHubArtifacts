package dev.sanmer.github.artifacts.ktx

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent

fun Context.viewUrl(url: String) {
    startActivity(
        Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
    )
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}