package top.fatweb.oxygen.toolbox.data.tool.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity

@Dao
interface ToolDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tool: ToolWithDistEntity)

    @Update
    suspend fun update(tool: ToolWithDistEntity)

    @Delete
    suspend fun deleteTool(tool: ToolWithDistEntity)

    @Query(
        """
            DELETE FROM tool
            WHERE authorUsername = :username
                and toolId = :toolId
        """
    )
    suspend fun deleteTool(username: String, toolId: String)

    @Query(
        """
            SELECT * FROM tool
            WHERE id = :id
        """
    )
    fun selectById(id: Long): Flow<ToolWithDistEntity?>

    @Query(
        """
            SELECT * FROM tool
            WHERE :searchValue = ''
                OR name LIKE '%' || :searchValue || '%' COLLATE NOCASE
                OR keywords LIKE '%\"%' || :searchValue || '%\"%' COLLATE NOCASE
            ORDER BY publish DESC
        """
    )
    fun selectAll(searchValue: String): Flow<List<ToolWithDistEntity>>

    @Query(
        """
            SELECT * FROM tool
            WHERE isStar = 1
                AND (
                    :searchValue = ''
                    OR name LIKE '%' || :searchValue || '%' COLLATE NOCASE
                    OR keywords LIKE '%"%' || :searchValue || '%"%' COLLATE NOCASE
                )
            ORDER BY publish DESC
        """
    )
    fun selectStar(searchValue: String): Flow<List<ToolWithDistEntity>>

    @Query(
        """
            SELECT * FROM tool
            WHERE authorUsername = :username
                and toolId = :toolId LIMIT 1
        """
    )
    fun selectByUsernameAndToolId(username: String, toolId: String): Flow<ToolWithDistEntity?>

    @Query(
        """
            SELECT COUNT(*) FROM tool
            WHERE baseId = :baseId AND baseVersion = :baseVersion
        """
    )
    suspend fun countByBaseIdAndVersion(baseId: Long, baseVersion: Long): Long

    @Query(
        """
            SELECT COUNT(*) FROM tool
            WHERE dist = :hash
        """
    )
    suspend fun countByDistHash(hash: String): Long
}
