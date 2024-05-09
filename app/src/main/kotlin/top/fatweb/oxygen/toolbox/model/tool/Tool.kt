package top.fatweb.oxygen.toolbox.model.tool

import kotlinx.datetime.LocalDateTime

data class Tool(
    val id: Long,

    val name: String,

    val toolId: String,

    val icon: String,

    val platform: Platform,

    val description: String,

    val base: String? = null,

    val author: Author,

    val ver: String,

    val keywords: List<String>,

    val categories: List<String>,

    val source: String? = null,

    val dist: String? = null,

    val entryPoint: String,

    val createTime: LocalDateTime,

    val updateTime: LocalDateTime
) {
    enum class Platform {
        WEB,

        DESKTOP,

        ANDROID
    }

    data class Author(
        val username: String,

        val nickname: String,

        val avatar: String
    )
}
