package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserWithInfoVo(
    val id: Long,

    val username: String,

    val userInfo: UserInfoVo
) {
    @Serializable
    data class UserInfoVo(
        val id: Long,

        val nickname: String,

        val avatar: String
    )
}