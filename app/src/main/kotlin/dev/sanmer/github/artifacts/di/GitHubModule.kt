package dev.sanmer.github.artifacts.di

import android.util.Log
import dev.sanmer.github.GitHub
import dev.sanmer.github.artifacts.BuildConfig
import dev.sanmer.github.serializer.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.serializersModuleOf
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

val GitHub = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            serializersModule = serializersModuleOf(InstantSerializer)
        }
    }

    single {
        OkHttpClient.Builder()
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
            .addInterceptor(
                HttpLoggingInterceptor {
                    Log.d("GitHub", it)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                request.header("X-GitHub-Api-Version", GitHub.API_VERSION)
                request.header("User-Agent", "GithubArtifacts/${BuildConfig.VERSION_CODE}")
                chain.proceed(request.build())
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .addConverterFactory(
                get<Json>().asConverterFactory("application/json; charset=UTF8".toMediaType())
            )
            .client(get())
            .baseUrl(GitHub.BASE_URL)
            .build()
    }

    single {
        get<Retrofit>().create<GitHub>()
    }
}