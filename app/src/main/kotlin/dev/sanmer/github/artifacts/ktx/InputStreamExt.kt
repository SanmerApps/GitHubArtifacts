package dev.sanmer.github.artifacts.ktx

import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest

@OptIn(ExperimentalStdlibApi::class)
fun InputStream.copyToWithSHA256(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    onProgress: (Long) -> Unit = {}
): String {
    val digest = MessageDigest.getInstance("SHA-256")
    var bytesCopied = 0L
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        digest.update(buffer, 0, bytes)
        bytesCopied += bytes
        onProgress(bytesCopied)
        bytes = read(buffer)
    }
    return digest.digest().toHexString()
}