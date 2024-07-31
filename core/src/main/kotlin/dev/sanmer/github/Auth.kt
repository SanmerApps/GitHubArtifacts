package dev.sanmer.github

import okhttp3.Request

sealed class Auth(val token: String) {
    data object None : Auth("")
    class Bearer(token: String) : Auth(token);

    companion object {
        fun Request.Builder.addAuth(auth: Auth): Request.Builder {
            return when (auth) {
                is Bearer -> header("Authorization", "Bearer ${auth.token}")
                else -> this
            }
        }
    }
}