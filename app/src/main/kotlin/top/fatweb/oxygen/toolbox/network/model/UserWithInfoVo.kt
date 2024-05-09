package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.model.tool.Tool

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

fun UserWithInfoVo.asExternalModel() = Tool.Author(
    username = username,
    nickname = userInfo.nickname,
    avatar = userInfo.avatar
)
