package top.fatweb.oxygen.toolbox.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    val name: String,

    val url: String? = null
)
