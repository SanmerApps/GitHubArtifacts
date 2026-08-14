package dev.sanmer.github.artifacts.ktx

import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest

inline fun InputStream.copyToWithSHA256(
    out: OutputStream,
    onProgress: (Long) -> Unit = {}
): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    var bytesCopied = 0L
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        digest.update(buffer, 0, bytes)
        bytesCopied += bytes
        onProgress(bytesCopied)
        bytes = read(buffer)
    }
    return digest.digest()
}