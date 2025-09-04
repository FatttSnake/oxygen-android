package top.fatweb.oxygen.toolbox.model.tool

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import top.fatweb.oxygen.toolbox.model.Converters
import top.fatweb.oxygen.toolbox.network.model.Platform

@Entity(tableName = "tool")
@TypeConverters(Converters::class)
data class ToolWithDistEntity(
    @PrimaryKey
    override val id: Long,

    override val name: String,

    override val toolId: String,

    override val icon: String,

    override val platform: Platform,

    override val description: String? = null,

    val baseId: Long,

    val baseVersion: Long,

    override val authorUsername: String,

    override val authorNickname: String,

    override val authorAvatar: String,

    override val ver: String,

    override val keywords: List<String>,

    override val categories: List<String>,

    val dist: String,

    override val entryPoint: String,

    override val publish: Long,

    val isStar: Boolean = false,

    @ColumnInfo(defaultValue = "NULL")
    var upgrade: String? = null
) : ToolCommonEntity {
    @Ignore
    override var installedVersion: String? = null
}
