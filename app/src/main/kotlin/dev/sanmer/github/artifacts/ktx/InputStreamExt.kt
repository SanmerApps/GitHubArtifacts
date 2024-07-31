package dev.sanmer.github.artifacts.ktx

import java.io.InputStream
import java.io.OutputStream

fun InputStream.copyTo(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    onCopied: (Long) -> Unit = {}
): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        onCopied(bytesCopied)
        bytes = read(buffer)
    }
    return bytesCopied
}