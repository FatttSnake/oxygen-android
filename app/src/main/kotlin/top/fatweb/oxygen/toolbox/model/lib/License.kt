package top.fatweb.oxygen.toolbox.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class License(
    val name: String,

    val url: String? = null,

    val year: String? = null,

    val content: String? = null,

    val internalHash: String? = null,

    val hash: String,

    val spdxId: String? = null
)
