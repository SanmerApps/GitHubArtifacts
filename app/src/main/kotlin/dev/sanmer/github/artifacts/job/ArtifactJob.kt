package dev.sanmer.github.artifacts.job

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.format.Formatter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dev.sanmer.github.GitHub.Default.toBearerAuth
import dev.sanmer.github.artifacts.Const
import dev.sanmer.github.artifacts.Logger
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.compat.BuildCompat
import dev.sanmer.github.artifacts.compat.PermissionCompat
import dev.sanmer.github.artifacts.ktx.copyToWithSHA256
import dev.sanmer.github.response.artifact.Artifact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class ArtifactJob : LifecycleService(), KoinComponent {
    private val okhttp by inject<OkHttpClient>()
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    private val runningMutex = Mutex()
    private val runningJob = mutableListOf<Long>()

    private val logger = Logger.Android("ArtifactJob")

    private suspend inline fun autoStopSelf(artifact: Artifact, block: (Artifact) -> Unit) {
        if (!runningMutex.withLock {
                runningJob.contains(artifact.id).also {
                    if (!it) runningJob.add(artifact.id)
                }
            }) {
            block(artifact)
            if (runningMutex.withLock {
                    runningJob.remove(artifact.id)
                    runningJob.isEmpty()
                }) {
                delay(5.seconds)
                if (runningMutex.withLock { runningJob.isEmpty() }) stopSelf()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun notify(
        id: Int,
        builder: NotificationCompat.Builder,
        block: NotificationCompat.Builder.() -> NotificationCompat.Builder
    ) = notificationManager.notify(id, builder.block().build())

    override fun onCreate() {
        logger.d("onCreate")
        super.onCreate()

        val builder = NotificationCompat.Builder(this, Const.CHANNEL_ID_ARTIFACT_JOB)
            .setSmallIcon(R.drawable.box)
            .setContentTitle(getText(R.string.artifact_job))
            .setSilent(true)
            .setOngoing(true)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
        ServiceCompat.startForeground(
            this,
            builder.hashCode(),
            builder.build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    override fun onDestroy() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        logger.d("onDestroy")
        super.onDestroy()
    }

    override fun onTimeout(startId: Int) {
        stopSelf()
        super.onTimeout(startId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleScope.launch {
            val token = intent?.token ?: return@launch
            autoStopSelf(intent.artifact) { artifact ->
                val builder = NotificationCompat.Builder(
                    applicationContext,
                    Const.CHANNEL_ID_ARTIFACT_JOB
                ).apply {
                    setSmallIcon(R.drawable.box)
                    setContentTitle(artifact.name)
                    setOngoing(true)
                    setSilent(true)
                    setGroup(GROUP_KEY)
                }

                val url = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.DISPLAY_NAME, artifact.name)
                }
                val uri = contentResolver.insert(url, values) ?: return@autoStopSelf

                runCatching {
                    jobState.update { JobState.Pending(artifact.id) }
                    notify(startId, builder) {
                        setProgress(1, 0, false)
                    }

                    val mimeType = download(
                        token = token,
                        artifact = artifact,
                        uri = uri,
                        startId = startId,
                        builder = builder
                    )

                    jobState.update { JobState.Success(artifact.id, uri, mimeType) }
                    notify(startId, builder) {
                        setProgress(0, 0, false)
                        setContentText(
                            Formatter.formatFileSize(applicationContext, artifact.sizeInBytes)
                        )
                        setContentIntent(viewUri(uri, mimeType))
                        setAutoCancel(true)
                        setOngoing(false)
                        setSilent(false)
                        setGroup(null)
                    }
                }.onFailure { error ->
                    logger.e(error)
                    contentResolver.delete(uri, null)

                    jobState.update { JobState.Failure(artifact.id, error) }
                    notify(startId, builder) {
                        setProgress(0, 0, false)
                        setContentText(getString(R.string.artifact_failed))
                        setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(error.message ?: error.javaClass.name)
                        )
                        setOngoing(false)
                        setSilent(false)
                        setGroup(null)
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun download(
        token: String,
        artifact: Artifact,
        uri: Uri,
        startId: Int,
        builder: NotificationCompat.Builder
    ) = withContext(Dispatchers.IO) {
        val request = Request(
            url = artifact.archiveDownloadUrl.toHttpUrl(),
            headers = Headers.headersOf("Authorization", token.toBearerAuth())
        )
        val response = okhttp.newCall(request).execute()
        require(response.code == 200) { "Expect code = 200" }
        val body = requireNotNull(response.body) { "Expect body" }
        val output = requireNotNull(contentResolver.openOutputStream(uri)) { "Expect output" }

        val period = 1.seconds
        var lastNotify = TimeSource.Monotonic.markNow()
        val sizeBytesF = artifact.sizeInBytes.toFloat()
        val sizeBytesI = artifact.sizeInBytes.toInt()
        val onProgress: (Long) -> Unit = { copied ->
            jobState.tryEmit(JobState.Running(artifact.id, copied / sizeBytesF))
            if (lastNotify.elapsedNow() >= period) {
                notify(startId, builder) {
                    setProgress(sizeBytesI, copied.toInt(), false)
                }
                lastNotify = TimeSource.Monotonic.markNow()
            }
        }
        val digest = body.byteStream().buffered().use { input ->
            input.copyToWithSHA256(output, onProgress).toHexString()
        }
        output.close()

        val target = artifact.digest.removePrefix("sha256:")
        if (target != artifact.digest) {
            check(digest == target) { "Expect SHA-256 = $target, but $digest" }
        }

        val contentType = when (val type = body.contentType().toString()) {
            "application/octet-stream" -> when (artifact.name.substringAfterLast('.', "")) {
                "apk" -> "application/vnd.android.package-archive"
                else -> type
            }

            else -> type
        }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.MIME_TYPE, contentType)
        }
        if (contentType == "application/zip" && !artifact.name.endsWith(".zip")) {
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "${artifact.name}.zip")
        }
        contentResolver.update(uri, values, null)

        contentType
    }

    private fun viewUri(uri: Uri, mimeType: String): PendingIntent? {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    sealed interface JobState {
        val id: Long

        data object Empty : JobState {
            override val id = 0L
        }

        data class Pending(
            override val id: Long
        ) : JobState

        data class Running(
            override val id: Long,
            val progress: Float
        ) : JobState

        data class Success(
            override val id: Long,
            val uri: Uri,
            val mimeType: String
        ) : JobState

        data class Failure(
            override val id: Long,
            val error: Throwable
        ) : JobState
    }

    companion object Default {
        private const val GROUP_KEY = "dev.sanmer.github.artifacts.ARTIFACT_JOB_GROUP_KEY"
        private const val EXTRA_ARTIFACT = "dev.sanmer.github.artifacts.extra.ARTIFACT"
        private const val EXTRA_TOKEN = "dev.sanmer.github.artifacts.extra.TOKEN"

        private fun Intent.putArtifact(value: Artifact) {
            putExtra(EXTRA_ARTIFACT, Json.encodeToString(value))
        }

        private val Intent.artifact: Artifact
            inline get() = Json.decodeFromString(getStringExtra(EXTRA_ARTIFACT).orEmpty())

        private fun Intent.putToken(value: String) {
            putExtra(EXTRA_TOKEN, value)
        }

        private val Intent.token: String
            inline get() = checkNotNull(getStringExtra(EXTRA_TOKEN))

        private val jobState = MutableStateFlow<JobState>(JobState.Empty)
        fun getJobState(artifactId: Long) = jobState.filter { it.id == artifactId }

        fun start(
            context: Context,
            artifact: Artifact,
            token: String
        ) {
            fun start() {
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