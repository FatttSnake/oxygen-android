package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseEntity

@Serializable
data class ToolBaseVo(
    val id: Long,

    val name: String,

    val version: Long
)

fun ToolBaseVo.asExternalModel() = ToolBaseEntity(
    id = id,
    name = name,
    version = version
)
