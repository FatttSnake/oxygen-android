package top.fatweb.oxygen.toolbox.network.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.network.serializer.LocalDateTimeSerializer

@Serializable
data class ToolCategoryVo(
    val id: Long,

    val name: String,

    val enable: Boolean,

    @Serializable(LocalDateTimeSerializer::class)
    val createTime: LocalDateTime,

    @Serializable(LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime
)
