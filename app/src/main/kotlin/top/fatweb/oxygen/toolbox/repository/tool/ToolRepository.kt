package top.fatweb.oxygen.toolbox.repository.tool

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity

interface ToolRepository {
    val toolViewTemplate: Flow<String>

    fun getAllToolsStream(searchValue: String): Flow<List<ToolEntity>>

    fun getToolById(id: Long): Flow<ToolEntity?>

    fun getToolByUsernameAndToolId(username: String, toolId: String): Flow<ToolEntity?>

    suspend fun saveTool(toolEntity: ToolEntity)

    suspend fun updateTool(toolEntity: ToolEntity)

    suspend fun removeTool(toolEntity: ToolEntity)
}