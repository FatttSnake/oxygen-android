package top.fatweb.oxygen.toolbox.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class Library(
    val uniqueId: String,

    val artifactVersion: String? = null,

    val name: String? = null,

    val description: String? = null,

    val website: String? = null,

    val developers: List<Developer>,

    val organization: Organization? = null,

    val scm: Scm? = null,

    val licenses: List<String>,

    val funding: List<Funding>,

    val tag: String? = null
)
