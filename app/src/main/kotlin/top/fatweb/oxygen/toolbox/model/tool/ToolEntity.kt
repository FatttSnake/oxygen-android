package top.fatweb.oxygen.toolbox.model.tool

import top.fatweb.oxygen.toolbox.network.model.Platform

data class ToolEntity(
    override val id: Long,

    override val name: String,

    override val toolId: String,

    override val icon: String,

    override val platform: Platform,

    override val description: String? = null,

    val base: ToolBaseEntity,

    override val authorUsername: String,

    override val authorNickname: String,

    override val authorAvatar: String,

    override val ver: String,

    override val keywords: List<String>,

    override val categories: List<String>,

    override val entryPoint: String,

    override val publish: Long,

    override var installedVersion: String? = null
) : ToolCommonEntity
