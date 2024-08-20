package top.fatweb.oxygen.toolbox.data.tool.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity

@Dao
interface ToolDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTool(tool: ToolEntity)

    @Update
    suspend fun updateTool(tool: ToolEntity)

    @Delete
    suspend fun deleteTool(tool: ToolEntity)

    @Query("SELECT * FROM tools " +
            "WHERE id = :id")
    fun selectToolById(id: Long): Flow<ToolEntity?>

    @Query("SELECT * FROM tools " +
            "WHERE :searchValue = '' " +
            "OR name LIKE '%' || :searchValue || '%' COLLATE NOCASE " +
            "OR keywords LIKE '%\"%' || :searchValue || '%\"%' COLLATE NOCASE " +
            "ORDER BY updateTime DESC")
    fun selectAllTools(searchValue: String): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools " +
            "WHERE isStar = 1 " +
            "AND (:searchValue = '' " +
            "OR name LIKE '%' || :searchValue || '%' COLLATE NOCASE " +
            "OR keywords LIKE '%\"%' || :searchValue || '%\"%' COLLATE NOCASE" +
            ") " +
            "ORDER BY updateTime DESC")
    fun selectStarTools(searchValue: String): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools " +
            "WHERE authorUsername = :username " +
            "and toolId = :toolId LIMIT 1")
    fun selectToolByUsernameAndToolId(username: String, toolId: String): Flow<ToolEntity?>
}