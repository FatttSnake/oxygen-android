package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ToolDistVo(
    val id: Long,

    val fileContent: String,

    val fileSize: Long
)
