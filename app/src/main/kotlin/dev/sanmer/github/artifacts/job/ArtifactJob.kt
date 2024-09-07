package dev.sanmer.github.artifacts.job

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.sanmer.github.GitHubHandler
import dev.sanmer.github.artifacts.Const
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.compat.BuildCompat
import dev.sanmer.github.artifacts.compat.MediaStoreCompat.createMediaStoreUri
import dev.sanmer.github.artifacts.compat.PermissionCompat
import dev.sanmer.github.artifacts.ktx.copyTo
import dev.sanmer.github.response.Artifact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Request
import timber.log.Timber
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
@AndroidEntryPoint
class ArtifactJob : LifecycleService() {
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    init {
        lifecycleScope.launch {
            while (currentCoroutineContext().isActive) {
                if (pendingJobs.isEmpty()) stopSelf()
                delay(5.seconds)
            }
        }
    }

    override fun onCreate() {
        Timber.d("onCreate")
        super.onCreate()
        setForeground()

        lifecycleScope.launch {
            jobStateFlow.onEach {
                when (it) {
                    is JobState.Pending -> notifyProgress(it.artifact, 0f)
                    is JobState.Success -> notifySuccess(it.artifact, it.uri)
                    is JobState.Failure -> notifyFailure(it.artifact)
                    else -> {}
                }
            }.filterIsInstance<JobState.Running>()
                .sample(500.milliseconds)
                .collect {
                    notifyProgress(it.artifact, it.progress)
                }
        }
    }

    override fun onDestroy() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        Timber.d("onDestroy")
        super.onDestroy()
    }

    override fun onTimeout(startId: Int) {
        stopSelf()
        super.onTimeout(startId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleScope.launch {
            val artifact = intent?.artifact ?: return@launch
            val token = intent.token

            runCatching {
                download(artifact, token)
            }.onSuccess { uri ->
                jobStateFlow.update { JobState.Success(artifact, uri) }
            }.onFailure { error ->
                Timber.e(error)
                jobStateFlow.update { JobState.Failure(artifact, error) }
            }

            pendingJobs.remove(artifact.id)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun download(artifact: Artifact, token: String) = withContext(Dispatchers.IO) {
        val uri = createMediaStoreUri(
            file = File(Environment.DIRECTORY_DOWNLOADS, "${artifact.name}.zip"),
            mimeType = "application/zip"
        )

        val request = Request.Builder()
            .url(artifact.archiveDownloadUrl)
            .build()

        val response = GitHubHandler(token).call(request)
        require(response.code == 200) { "Expect code = 200" }
        require(response.headers["Content-Type"] == "zip") { "Expect Content-Type = zip" }
        val body = requireNotNull(response.body) { "Expect body" }

        contentResolver.openOutputStream(uri).let(::requireNotNull).use { output ->
            body.byteStream().buffered().use { input ->
                input.copyTo(output) { bytesCopied ->
                    val progress = bytesCopied / artifact.sizeInBytes.toFloat()
                    jobStateFlow.update { JobState.Running(artifact, progress) }
                }
            }
        }

        uri
    }

    private fun setForeground() {
        val notification = newNotificationBuilder()
            .setContentTitle(getText(R.string.artifact_job))
            .setSilent(true)
            .setOngoing(true)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .build()

        ServiceCompat.startForeground(
            this,
            notification.hashCode(),
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    private fun notifyProgress(artifact: Artifact, progress: Float) {
        if (progress == 1f) return
        val notification = newNotificationBuilder()
            .setContentTitle(artifact.name)
            .setProgress(100, (100 * progress).toInt(), false)
            .setSilent(true)
            .setOngoing(true)
            .setGroup(GROUP_KEY)
            .build()

        notify(artifact.id.toInt(), notification)
    }

    private fun notifySuccess(artifact: Artifact, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/zip")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pending = PendingIntent.getActivity(this, 0, intent, flag)

        val notification = newNotificationBuilder()
            .setContentTitle(artifact.name)
            .setContentText(getText(R.string.download_success))
            .setContentIntent(pending)
            .setSilent(true)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        notify(artifact.id.toInt(), notification)
    }

    private fun notifyFailure(artifact: Artifact) {
        val notification = newNotificationBuilder()
            .setContentTitle(artifact.name)
            .setContentText(getText(R.string.download_fail))
            .setSilent(false)
            .setOngoing(false)
            .build()

        notify(artifact.id.toInt(), notification)
    }

    private fun newNotificationBuilder() =
        NotificationCompat.Builder(this, Const.CHANNEL_ID_ARTIFACT_JOB)
            .setSmallIcon(R.drawable.box)

    @Throws(SecurityException::class)
    private fun notify(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
    }

    sealed class JobState(val id: Long) {
        data object Empty : JobState(0)
        class Pending(val artifact: Artifact) : JobState(artifact.id)
        class Running(val artifact: Artifact, val progress: Float) : JobState(artifact.id)
        class Success(val artifact: Artifact, val uri: Uri) : JobState(artifact.id)
        class Failure(val artifact: Artifact, val error: Throwable) : JobState(artifact.id)
    }

    companion object Default {
        private const val GROUP_KEY = "dev.sanmer.github.artifacts.ARTIFACT_JOB_GROUP_KEY"
        private const val EXTRA_ARTIFACT = "dev.sanmer.github.artifacts.extra.Artifact"
        private const val EXTRA_TOKEN = "dev.sanmer.github.artifacts.extra.Token"

        private fun Intent.putArtifact(value: Artifact) {
            putExtra(EXTRA_ARTIFACT, Json.encodeToString(value))
        }

        private val Intent.artifact: Artifact
            inline get() = Json.decodeFromString(checkNotNull(getStringExtra(EXTRA_ARTIFACT)))

        private fun Intent.putToken(value: String) {
            putExtra(EXTRA_TOKEN, value)
        }

        private val Intent.token: String
            inline get() = checkNotNull(getStringExtra(EXTRA_TOKEN))

        private val pendingJobs = mutableListOf<Long>()

        private val jobStateFlow = MutableStateFlow<JobState>(JobState.Empty)

        fun getJobState(artifactId: Long): Flow<JobState> {
            return jobStateFlow.filter { it.id == artifactId }
        }

        fun start(
            context: Context,
            artifact: Artifact,
            token: String
        ) {
            fun start() {
                if (pendingJobs.contains(artifact.id)) return
                pendingJobs.add(artifact.id)
                jobStateFlow.update { JobState.Pending(artifact) }
                context.startService(
                    Intent(context, ArtifactJob::class.java).also {
                        it.putArtifact(artifact)
                        it.putToken(token)
                    }
                )
            }

            if (BuildCompat.atLeastT) {
                PermissionCompat.requestPermission(
                    context = context,
                    permission = Manifest.permission.POST_NOTIFICATIONS
                ) { allowed ->
                    if (allowed) start()
                }
            } else {
                start()
            }
        }
    }
}