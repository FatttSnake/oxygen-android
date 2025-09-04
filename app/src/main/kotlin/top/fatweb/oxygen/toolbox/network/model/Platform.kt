package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
