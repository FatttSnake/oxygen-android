package top.fatweb.oxygen.toolbox.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Base64.decodeToString(base64String: String): String =
    this.decode(base64String).decodeToString()

@OptIn(ExperimentalEncodingApi::class)
fun Base64.decodeToByteArray(base64String: String): ByteArray =
    this.decode(base64String)
