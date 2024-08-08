package top.fatweb.oxygen.toolbox.model

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity.Platform

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromPlatform(platform: Platform): String = platform.name

    @TypeConverter
    fun toPlatform(name: String): Platform = Platform.valueOf(name)

    @TypeConverter
    fun fromStringList(stringList: List<String>): String = json.encodeToString(stringList)

    @TypeConverter
    fun toStringList(stringList: String): List<String> = json.decodeFromString(stringList)

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): String = localDateTime.toString()

    @TypeConverter
    fun toLocalDateTime(string: String): LocalDateTime = LocalDateTime.parse(string)
}