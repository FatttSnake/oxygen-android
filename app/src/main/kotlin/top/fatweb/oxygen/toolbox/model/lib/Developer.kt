package top.fatweb.oxygen.toolbox.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class Developer(
    val name: String? = null,

    val organisationUrl: String? = null
)
