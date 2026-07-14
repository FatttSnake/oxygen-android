package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity

@Serializable
data class ToolBaseWithDistVo(
    val id: Long,

    val name: String,

    val dist: ToolDistVo,

    val platform: Platform,

    val version: Long
)

fun ToolBaseWithDistVo.asExternalModel() = ToolBaseWithDistEntity(
    id = id,
    name = name,
    dist = dist.fileContent,
    platform = platform,
    version = version
)
