package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity

@Serializable
data class ToolVo(
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

    val entryPoint: String,

    val publish: Long
)

fun ToolVo.asExternalModel() = ToolEntity(
    id = id,
    name = name,
    toolId = toolId,
    icon = icon,
    platform = platform,
    description = description,
    base = base.asExternalModel(),
    authorUsername = author.username,
    authorNickname = author.userInfo.nickname,
    authorAvatar = author.userInfo.avatar,
    ver = ver,
    keywords = keywords,
    categories = categories.map { it.name },
    entryPoint = entryPoint,
    publish = publish
)
