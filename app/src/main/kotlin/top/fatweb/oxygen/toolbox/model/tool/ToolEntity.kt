package top.fatweb.oxygen.toolbox.model.tool

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.LocalDateTime
import top.fatweb.oxygen.toolbox.model.Converters

@Entity(tableName = "tools")
@TypeConverters(Converters::class)
data class ToolEntity(
    @PrimaryKey
    val id: Long,

    val name: String,

    val toolId: String,

    val icon: String,

    val platform: Platform,

    val description: String? = null,

    val base: String? = null,

    val authorUsername: String,

    val authorNickname: String,

    val authorAvatar: String,

    val ver: String,

    val keywords: List<String>,

    val categories: List<String>,

    val source: String? = null,

    val dist: String? = null,

    val entryPoint: String,

    val createTime: LocalDateTime,

    val updateTime: LocalDateTime,

    val isStar: Boolean = false,

    val isInstalled: Boolean = false,

    @ColumnInfo(defaultValue = "NULL")
    val upgrade: String? = null
) {
    constructor(toolId: String, authorUsername: String, ver: String, upgrade: String? = null) :
            this(
                id = -1,
                name = "Unknown",
                toolId = toolId,
                icon = "",
                platform = Platform.Android,
                authorUsername = authorUsername,
                authorNickname = "Unknown",
                authorAvatar = "",
                ver = ver,
                keywords = emptyList(),
                categories = emptyList(),
                entryPoint = "",
                createTime = LocalDateTime(1970, 1, 1, 0, 0),
                updateTime = LocalDateTime(1970, 1, 1, 0, 0),
                upgrade = upgrade
            )

    enum class Platform {
        Web,

        Desktop,

        Android
    }
}
