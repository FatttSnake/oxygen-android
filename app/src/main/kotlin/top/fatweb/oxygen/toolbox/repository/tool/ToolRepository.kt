package top.fatweb.oxygen.toolbox.repository.tool

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity

interface ToolRepository {
    val toolViewTemplate: Flow<String>

    fun getGlobalJsVariables(isDarkMode: Boolean): Flow<String>

    fun getGlobalCssVariables(isDarkMode: Boolean): Flow<String>

    fun getAllToolsStream(searchValue: String): Flow<List<ToolWithDistEntity>>

    fun getStarToolsStream(searchValue: String): Flow<List<ToolWithDistEntity>>

    fun getToolById(id: Long): Flow<ToolWithDistEntity?>

    fun getToolByUsernameAndToolId(username: String, toolId: String): Flow<ToolWithDistEntity?>

    suspend fun saveTool(toolWithDistEntity: ToolWithDistEntity)

    suspend fun updateTool(toolWithDistEntity: ToolWithDistEntity)

    suspend fun removeTool(toolWithDistEntity: ToolWithDistEntity)

    suspend fun removeTool(username: String, toolId: String)

    fun getToolBaseByIdAndVersion(id: Long, version: Long): Flow<ToolBaseWithDistEntity?>

    suspend fun saveToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity)

    suspend fun updateToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity)

    suspend fun removeToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity)

    suspend fun clearToolBaseCache()
}
