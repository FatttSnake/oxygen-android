package top.fatweb.oxygen.toolbox.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class Scm(
    val connection: String? = null,

    val developerConnection: String? = null,

    val url: String? = null
)
