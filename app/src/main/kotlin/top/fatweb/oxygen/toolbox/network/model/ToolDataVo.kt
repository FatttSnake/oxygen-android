package top.fatweb.oxygen.toolbox.network.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.network.serializer.LocalDateTimeSerializer

@Serializable
data class ToolDataVo(
    val id: Long,

    val data: String,

    @Serializable(LocalDateTimeSerializer::class)
    val createTime: LocalDateTime? = null,

    @Serializable(LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime? = null
)
