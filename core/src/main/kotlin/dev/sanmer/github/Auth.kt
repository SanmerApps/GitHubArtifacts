package dev.sanmer.github

import okhttp3.Request

sealed class Auth {
    data object None : Auth()
    class Bearer(val token: String) : Auth()

    override fun toString(): String {
        return when (this) {
            is Bearer -> token
            None -> "null"
        }
    }

    companion object Default {
        fun String.toBearerAuth() = Bearer(this)

        internal fun Request.Builder.addAuth(auth: Auth): Request.Builder {
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