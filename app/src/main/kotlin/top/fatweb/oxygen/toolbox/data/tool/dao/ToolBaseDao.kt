package top.fatweb.oxygen.toolbox.data.tool.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity

@Dao
interface ToolBaseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(toolBase: ToolBaseWithDistEntity)

    @Update
    suspend fun update(toolBase: ToolBaseWithDistEntity)

    @Delete
    suspend fun delete(toolBase: ToolBaseWithDistEntity)

    @Query(
        """
            SELECT * FROM tool_base
            WHERE baseId = :id and version = :version
        """
    )
    fun selectByIdAndVersion(id: Long, version: Long): Flow<ToolBaseWithDistEntity?>

    @Query(
        """
            UPDATE tool_base SET isCache = 1
            WHERE baseId = :id AND version = :version
        """
    )
    suspend fun markAsCache(id: Long, version: Long)

    @Query(
        """
            DELETE FROM tool_base
            WHERE isCache = 1
        """
    )
    suspend fun clearCache()
}
