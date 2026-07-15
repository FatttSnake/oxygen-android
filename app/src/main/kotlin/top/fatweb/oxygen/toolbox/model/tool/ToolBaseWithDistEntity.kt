package top.fatweb.oxygen.toolbox.model.tool

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import top.fatweb.oxygen.toolbox.model.Converters
import top.fatweb.oxygen.toolbox.network.model.Platform

@Entity(tableName = "tool_base")
@TypeConverters(Converters::class)
data class ToolBaseWithDistEntity(
    @PrimaryKey
    val id: String,

    val baseId: Long,

    val name: String,

    val dist: String,

    val platform: Platform,

    val version: Long,

    @ColumnInfo(defaultValue = "1")
    var isCache: Boolean = true
)
