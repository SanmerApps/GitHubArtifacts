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
import dev.sanmer.github.artifacts.ktx.dropWithin
import dev.sanmer.github.artifacts.ktx.shortId
import dev.sanmer.github.response.artifact.Artifact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ArtifactJob : LifecycleService(), KoinComponent {
    private val okhttp by inject<OkHttpClient>()
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    private val logger = Logger.Android("ArtifactJob")

    @SuppressLint("MissingPermission")
    private inline fun notify(id: Int, block: NotificationCompat.Builder.() -> Unit) {
        val builder = NotificationCompat.Builder(this, Const.CHANNEL_ID_ARTIFACT_JOB)
        builder.setSmallIcon(R.drawable.box)
        builder.block()
        notificationManager.notify(id, builder.build())
    }

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

        lifecycleScope.launch {
            jobState.dropWithin(500.milliseconds) {
                it is JobState.Running
            }.collect {
                when (it) {
                    JobState.Empty -> {}
                    is JobState.Pending -> notify(it.shortId) {
                        setContentTitle(it.artifact.name)
                        setProgress(100, 0, false)
                        setSilent(true)
                        setOngoing(true)
                        setGroup(GROUP_KEY)
                    }

                    is JobState.Running -> notify(it.shortId) {
                        setContentTitle(it.artifact.name)
                        setProgress(it.artifact.sizeInBytes.toInt(), it.copied.toInt(), false)
                        setSilent(true)
                        setOngoing(true)
                        setGroup(GROUP_KEY)
                    }

                    is JobState.Success -> notify(it.shortId) {
                        setContentTitle(it.artifact.name)
                        setContentText(
                            Formatter.formatFileSize(applicationContext, it.artifact.sizeInBytes)
                        )
                        setContentIntent(viewUri(it.uri, it.mimeType))
                        setSilent(true)
                        setAutoCancel(true)
                    }

                    is JobState.Failure -> notify(it.shortId) {
                        setContentTitle(it.artifact.name)
                        setContentText(it.error.message ?: it.error.javaClass.name)
                        setSilent(false)
                    }
                }
            }
        }
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
            val artifact = intent.artifact

            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val entry = ContentValues().apply {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.DISPLAY_NAME, artifact.name)
            }
            val uri = requireNotNull(contentResolver.insert(collection, entry))

            runCatching {
                val mimeType = downloadArtifact(
                    token = token,
                    artifact = artifact,
                    uri = uri
                )
                jobState.update { JobState.Success(artifact, uri, mimeType) }
            }.onFailure { error ->
                logger.e(error)
                jobState.update { JobState.Failure(artifact, error) }
                contentResolver.delete(uri, null)
            }

            pendingJobs.remove(artifact.id)
            if (pendingJobs.isEmpty()) {
                delay(5.seconds)
                if (pendingJobs.isEmpty()) stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun downloadArtifact(
        token: String,
        artifact: Artifact,
        uri: Uri
    ) = withContext(Dispatchers.IO) {
        val request = Request(
            url = artifact.archiveDownloadUrl.toHttpUrl(),
            headers = Headers.headersOf("Authorization", token.toBearerAuth())
        )
        val response = okhttp.newCall(request).execute()
        require(response.code == 200) { "Expect code = 200" }
        val body = requireNotNull(response.body) { "Expect body" }
        val output = requireNotNull(contentResolver.openOutputStream(uri)) { "Expect output" }
        val digest = body.byteStream().buffered().use { input ->
            input.copyToWithSHA256(output) { jobState.tryEmit(JobState.Running(artifact, it)) }
                .toHexString()
        }
        output.close()

        logger.d("Content-Type = ${body.contentType()}, SHA-256 = $digest")
        val target = artifact.digest.removePrefix("sha256:")
        if (target != artifact.digest) {
            require(digest == target) { "Expect SHA-256 = $target, but $digest" }
        }

        val contentType = when (val type = body.contentType().toString()) {
            "application/octet-stream" -> when (artifact.name.substringAfterLast('.', "")) {
                "apk" -> "application/vnd.android.package-archive"
                else -> type
            }

            else -> type
        }
        val entry = ContentValues()
        when (contentType) {
            "application/zip" if (!artifact.name.endsWith(".zip")) -> {
                entry.put(MediaStore.MediaColumns.DISPLAY_NAME, "${artifact.name}.zip")
                entry.put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            }

            else -> entry.put(MediaStore.MediaColumns.MIME_TYPE, contentType)
        }
        contentResolver.update(uri, entry, null)

        contentType
    }

    private fun viewUri(uri: Uri, mimeType: String): PendingIntent? {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getActivity(this, 0, intent, flag)
    }

    sealed class JobState(val id: Long) {
        val shortId by lazy { id.shortId() }

        data object Empty : JobState(0)

        class Pending(
            val artifact: Artifact
        ) : JobState(artifact.id)

        class Running(
            val artifact: Artifact,
            val copied: Long
        ) : JobState(artifact.id) {
            val progress get() = copied.toFloat() / artifact.sizeInBytes
        }

        class Success(
            val artifact: Artifact,
            val uri: Uri,
            val mimeType: String
        ) : JobState(artifact.id)

        class Failure(
            val artifact: Artifact,
            val error: Throwable
        ) : JobState(artifact.id)
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

        private val pendingJobs = mutableListOf<Long>()

        private val jobState = MutableStateFlow<JobState>(JobState.Empty)
        fun getJobState(artifactId: Long) = jobState.filter { it.id == artifactId }

        fun start(
            context: Context,
            artifact: Artifact,
            token: String
        ) {
            fun start() {
                if (pendingJobs.contains(artifact.id)) return
                pendingJobs.add(artifact.id)
                jobState.update { JobState.Pending(artifact) }
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