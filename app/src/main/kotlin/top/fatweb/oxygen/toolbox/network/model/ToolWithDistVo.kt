package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity

@Serializable
data class ToolWithDistVo(
    val id: Long,

    val name: String,

    val toolId: String,

    val icon: String,

    val platform: Platform,

    val description: String? = null,

    val base: ToolBaseVo,

    val author: UserWithInfoVo,

    val ver: String,

    val keywords: List<String>,

    val categories: List<ToolCategoryVo>,

    val dist: ToolDistVo,

    val entryPoint: String,

    val publish: Long
)

fun ToolWithDistVo.asExternalModel() = ToolWithDistEntity(
    id = id,
    name = name,
    toolId = toolId,
    icon = icon,
    platform = platform,
    description = description,
    baseId = base.id,
    baseVersion = base.version,
    authorUsername = author.username,
    authorNickname = author.userInfo.nickname,
    authorAvatar = author.userInfo.avatar,
    ver = ver,
    keywords = keywords,
    categories = categories.map { it.name },
    dist = dist.fileContent,
    entryPoint = entryPoint,
    publish = publish
)
