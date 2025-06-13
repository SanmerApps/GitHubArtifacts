package dev.sanmer.github

import android.util.Log
import dev.sanmer.github.Auth.Default.addAuth
import dev.sanmer.github.stub.Artifacts
import dev.sanmer.github.stub.Repositories
import dev.sanmer.github.stub.WorkflowRuns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.io.InputStream
import java.util.Locale

class GitHub(
    val baseUrl: String = BASE_URL,
    val auth: Auth
) {
    private val okhttp by lazy {
        createOkHttpClient {
            addHeader("X-GitHub-Api-Version", API_VERSION)
            addHeader("User-Agent", "GitHub/$API_VERSION")
            addAuth(auth)
        }.build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(
                JsonCompat.asConverterFactory("application/json; charset=UTF8".toMediaType())
            )
            .client(okhttp)
            .baseUrl(baseUrl)
            .build()
    }

    val repositories by lazy { retrofit.create<Repositories>() }
    val workflowRuns by lazy { retrofit.create<WorkflowRuns>() }
    val artifacts by lazy { retrofit.create<Artifacts>() }

    suspend fun <T> download(
        url: String,
        onStream: (InputStream) -> T
    ) = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        val response = okhttp.newCall(request).execute()
        require(response.code == 200) { "Expect code = 200" }
        val body = requireNotNull(response.body) { "Expect body" }
        body.byteStream().buffered().use(onStream)
    }

    companion object Default {
        const val API_VERSION = "2022-11-28"
        const val BASE_URL = "https://api.github.com/"

        fun createOkHttpClient(
            header: Request.Builder.() -> Request.Builder = { this }
        ) = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor {
                    Log.d("GitHub", it)
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
}