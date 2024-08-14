package top.fatweb.oxygen.toolbox.network.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.network.serializer.LocalDateTimeSerializer

@Serializable
data class ToolBaseVo(
    val id: Long,

    val name: String,

    val source: ToolDataVo? = null,

    val dist: ToolDataVo,

    val platform: Platform? = null,

    val compiled: Boolean? = null,

    @Serializable(LocalDateTimeSerializer::class)
    val createTime: LocalDateTime? = null,

    @Serializable(LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime? = null
) {
    @Serializable
    enum class Platform {
        @SerialName("WEB")
        Web,

        @SerialName("DESKTOP")
        Desktop,

        @SerialName("ANDROID")
        Android;

        override fun toString(): String =
            javaClass.getField(name).getAnnotation(SerialName::class.java)!!.value
    }
}

fun ToolBaseVo.Platform.asExternalModel() = ToolEntity.Platform.valueOf(this.name)
