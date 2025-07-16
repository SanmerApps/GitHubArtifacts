package dev.sanmer.github.artifacts

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dev.sanmer.github.artifacts.di.Database
import dev.sanmer.github.artifacts.di.Repositories
import dev.sanmer.github.artifacts.di.ViewModels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels(applicationContext)
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(Database, Repositories, ViewModels)
        }
    }

    private fun createNotificationChannels(context: Context) {
        val channels = listOf(
            NotificationChannel(
                Const.CHANNEL_ID_ARTIFACT_JOB,
                context.getString(R.string.artifact_job),
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        NotificationManagerCompat.from(context).apply {
            createNotificationChannels(channels)
            deleteUnlistedNotificationChannels(channels.map { it.id })
        }
    }
}