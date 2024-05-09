package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseResult<T>(
    val code: Long,

    val success: Boolean,

    val msg: String,

    val data: T? = null
)
