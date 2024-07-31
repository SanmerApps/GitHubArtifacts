package dev.sanmer.github

import android.util.Log
import dev.sanmer.github.Auth.Companion.addAuth
import okhttp3.ConnectionSpec
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.util.Locale

abstract class HttpHandler(
    private val baseUrl: String,
    private val auth: Auth
) {
    private val headers = hashMapOf<String, String>()

    val okhttp by lazy {
        createOkHttpClient {
            headers(headers.toHeaders())
            addAuth(auth)
        }.build()
    }

    internal val retrofit by lazy {
        createRetrofit()
            .client(okhttp)
            .baseUrl(baseUrl)
            .build()
    }

    fun addHeader(name: String, value: String) {
        headers[name] = value
    }

    internal inline fun <reified T : Any> create() = retrofit.create<T>()

    internal companion object Util {
        fun createOkHttpClient(
            header: Request.Builder.() -> Request.Builder = { this }
        ): OkHttpClient.Builder {
            return OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor {
                        Log.d("HttpHandler", it)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                )
                .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                    request.header("Accept-Language", Locale.getDefault().toLanguageTag())
                    chain.proceed(request.header().build())
                }
        }

        fun createRetrofit(): Retrofit.Builder {
            return Retrofit.Builder()
                .addConverterFactory(
                    JsonCompat.asConverterFactory("application/json; charset=UTF8".toMediaType())
                )
        }
    }
}