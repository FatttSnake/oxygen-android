package top.fatweb.oxygen.toolbox.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes


/**
 * Computes the SHA-256 key of this [ByteArray]
 *
 * @return A new [ByteArray] containing the SHA-256 hash (32 bytes)
 * @author FatttSnake, fatttsnake@gmail.com
 * @since 1.3.0
 * @see ByteArray
 */
fun ByteArray.sha256(): ByteArray =
    MessageDigest.getInstance("SHA-256").digest(this)

/**
 * Computes the SHA-256 hash of this [ByteArray] and returns it as a hexadecimal string
 *
 * @return A [String] representing the SHA-256 hash in lowercase hexadecimal format (64 characters)
 * @author FatttSnake, fatttsnake@gmail.com
 * @since 1.3.0
 * @see ByteArray
 */
fun ByteArray.sha256HexString(): String =
    this.sha256().toHexString()

/**
 * Compresses the data in this [ByteArray] using the specified output stream factory
 *
 * The factory function should wrap the given [OutputStream] with a compressing output stream
 * (e.g., [java.util.zip.GZIPOutputStream]). The entire content of this byte array
 * is written to the compressing stream and the compressed result is returned.
 *
 * @param streamFactory A function that wraps an [OutputStream] with compression logic
 * @return A new [ByteArray] containing the compressed data
 * @author FatttSnake, fatttsnake@gmail.com
 * @since 1.3.0
 * @see ByteArray
 * @see OutputStream
 */
fun ByteArray.compress(streamFactory: (OutputStream) -> OutputStream): ByteArray {
    ByteArrayOutputStream().use { output ->
        streamFactory(output).use { compressed ->
            compressed.write(this)
        }
        return output.toByteArray()
    }
}

/**
 * Decompresses the data in this [ByteArray] using the specified input stream factory
 *
 * The factory function should wrap the given [InputStream] with a decompressing input stream
 * (e.g., [java.util.zip.GZIPInputStream]). The entire content of the decompressing
 * stream is read and returned as a byte array.
 *
 * @param streamFactory A function that wraps an [InputStream] with decompression logic
 * @return A new [ByteArray] containing the decompressed data
 * @author FatttSnake, fatttsnake@gmail.com
 * @since 1.3.0
 * @see ByteArray
 * @see InputStream
 */
fun ByteArray.decompress(streamFactory: (InputStream) -> InputStream): ByteArray {
    ByteArrayInputStream(this).use { input ->
        streamFactory(input).use { compressed ->
            ByteArrayOutputStream().use { output ->
                compressed.copyTo(output)
                return output.toByteArray()
            }
        }
    }
}

/**
 * Saves this [ByteArray] to a file at the specified path, optionally applying compression
 *
 * The file is constructed from the [base] directory and [subpaths] components.
 *
 * If [compressStreamFactory] is provided, the data is compressed before being written using the
 * specified compression stream factory (e.g., `{ GZIPOutputStream(it) }`).
 *
 * @param base The base directory path
 * @param subpaths Additional path components to append to [base]
 * @param compressStreamFactory Optional factory for compressing the data before writing.
 *        If `null`, the raw bytes are written without compression
 * @return The [Path] to the saved file
 * @author FatttSnake, fatttsnake@gmail.com
 * @since 1.3.0
 * @see ByteArray
 * @see OutputStream
 * @see Path
 */
fun ByteArray.saveToFile(
    base: String,
    vararg subpaths: String,
    compressStreamFactory: ((OutputStream) -> OutputStream)? = null
): Path =
    Path(base = base, *subpaths).apply {
        val file = File(base, subpaths.joinToString(File.separator))
        file.parentFile?.mkdirs()

        writeBytes(
            compressStreamFactory
                ?.let { this@saveToFile.compress(it) }
                ?: this@saveToFile
        )
    }

/**
 * Safely saves this [ByteArray] to a file, wrapping the result in a [Result]
 *
 * This is a safe wrapper around [saveToFile] that catches exceptions and returns them
 * as a [Result.failure] instead of throwing.
 *
 * @param base The base directory path
 * @param subpaths Additional path components to append to [base]
 * @param compressStreamFactory Optional factory for compressing the data before writing.
 *        If `null`, the raw bytes are written without compression
 * @return A [Result] containing the [Path] of the saved file, or a failure if an error occurred
 * @author FatttSnake, fatttsnake@gmail.com
 * @since 1.3.0
 * @see saveToFile
 * @see ByteArray
 * @see OutputStream
 * @see Result
 * @see Path
 */
fun ByteArray.saveToFileSafe(
    base: String,
    vararg subpaths: String,
    compressStreamFactory: ((OutputStream) -> OutputStream)? = null
): Result<Path> = runCatching {
    this.saveToFile(base = base, *subpaths, compressStreamFactory = compressStreamFactory)
}

/**
 * Reads the contents of the file at this [Path] as a [ByteArray], optionally applying decompression
 *
 * If [decompressStreamFactory] is provided, the raw bytes read from the file are decompressed
 * using the specified decompression stream factory (e.g., `{ GZIPInputStream(it) }`).
 *
 * @param decompressStreamFactory Optional factory for decompressing the data after reading.
 *        If `null`, the raw bytes are returned without decompression.
 * @return A [ByteArray] containing the (possibly decompressed) file contents
 * @author FatttSnake, fatttsnake@gmail.com
 * @since 1.3.0
 * @see Path
 * @see InputStream
 * @see ByteArray
 */
fun Path.readFile(
    decompressStreamFactory: ((InputStream) -> InputStream)? = null
): ByteArray =
    decompressStreamFactory
        ?.let {
            this.readBytes().decompress(it)
        }
        ?: this.readBytes()

/**
 * Safely reads the contents of the file at this [Path] as a [ByteArray], wrapping the result in a [Result]
 *
 * This is a safe wrapper around [readFile] that catches exceptions and returns them
 * as a [Result.failure] instead of throwing.
 *
 * @param decompressStreamFactory Optional factory for decompressing the data after reading
 * @return A [Result] containing the file contents as a [ByteArray], or a failure if an error occurred
 * @author FatttSnake, fatttsnake@gmail.com
 * @since 1.3.0
 * @see readFile
 * @see Path
 * @see InputStream
 * @see Result
 * @see ByteArray
 */
fun Path.readFileSafe(
    decompressStreamFactory: ((InputStream) -> InputStream)? = null
): Result<ByteArray> = runCatching {
    this.readFile(decompressStreamFactory)
}
