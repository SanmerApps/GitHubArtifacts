package dev.sanmer.github

import okhttp3.Request

sealed class Auth {
    data object None : Auth()
    class Bearer(val token: String) : Auth()

    companion object Util {
        fun Request.Builder.addAuth(auth: Auth): Request.Builder {
            return when (auth) {
                is Bearer -> if (auth.token.isNotBlank()) {
                    header("Authorization", "Bearer ${auth.token}")
                } else {
                    this
                }

                else -> this
            }
        }
    }
}