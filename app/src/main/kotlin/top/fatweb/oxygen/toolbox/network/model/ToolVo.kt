package top.fatweb.oxygen.toolbox.network.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.model.tool.Tool
import top.fatweb.oxygen.toolbox.network.serializer.LocalDateTimeSerializer

@Serializable
data class ToolVo(
    val id: Long,

    val name: String,

    val toolId: String,

    val icon: String,

    val platform: ToolBaseVo.Platform,

    val description: String,

    val base: ToolBaseVo? = null,

    val author: UserWithInfoVo,

    val ver: String,

    val keywords: List<String>,

    val categories: List<ToolCategoryVo>,

    val source: ToolDataVo? = null,

    val dist: ToolDataVo? = null,

    val entryPoint: String,

    val publish: Long,

    val review: ReviewType,

    @Serializable(LocalDateTimeSerializer::class)
    val createTime: LocalDateTime,

    @Serializable(LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime
) {
    @Serializable
    enum class ReviewType {
        @SerialName("NONE")
        NONE,

        @SerialName("PROCESSING")
        PROCESSING,

        @SerialName("PASS")
        PASS,

        @SerialName("REJECT")
        REJECT
    }
}

fun ToolVo.asExternalModel() = Tool(
    id = id,
    name = name,
    toolId = toolId,
    icon = icon,
    platform = platform.asExternalModel(),
    description = description,
    base = base?.dist?.data,
    author = author.asExternalModel(),
    ver = ver,
    keywords = keywords,
    categories = categories.map { it.name },
    source = source?.data,
    dist = dist?.data,
    entryPoint = entryPoint,
    createTime = createTime,
    updateTime = updateTime
)
