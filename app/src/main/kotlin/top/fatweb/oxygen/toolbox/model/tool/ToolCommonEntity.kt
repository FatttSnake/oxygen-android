package top.fatweb.oxygen.toolbox.model.tool

import top.fatweb.oxygen.toolbox.network.model.Platform

interface ToolCommonEntity {
    val id: Long

    val name: String

    val toolId: String

    val icon: String

    val platform: Platform

    val description: String?

    val authorUsername: String

    val authorNickname: String

    val authorAvatar: String

    val ver: String

    val keywords: List<String>

    val categories: List<String>

    val entryPoint: String

    val publish: Long

    var installedVersion: String?
}
