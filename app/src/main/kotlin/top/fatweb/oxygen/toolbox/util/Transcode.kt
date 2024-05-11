package top.fatweb.oxygen.toolbox.util

import java.util.zip.Inflater
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Base64.decodeToString(base64String: String): String =
    this.decode(base64String).decodeToString()

@OptIn(ExperimentalEncodingApi::class)
fun Base64.decodeToByteArray(base64String: String): ByteArray =
    this.decode(base64String)

@OptIn(ExperimentalEncodingApi::class)
fun Base64.decodeToStringWithZip(base64String: String): String {
    val binary = this.decode(base64String).toString(Charsets.ISO_8859_1)

    // zlib header (x78), level 9 (xDA)
    if (binary.startsWith("\u0078\u00DA")) {
        val byteArray = binary.toByteArray(Charsets.ISO_8859_1)
        val inflater = Inflater().apply {
            setInput(byteArray)
        }
        val uncompressed = ByteArray(byteArray.size * 10)
        val resultLength = inflater.inflate(uncompressed)
        inflater.end()

        return String(uncompressed, 0, resultLength, Charsets.UTF_8)
    }

    return ""
}
