package top.fatweb.oxygen.toolbox.repository.tool.impl

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject

class OfflineToolRepository @Inject constructor(
    private val toolDao: ToolDao
) : ToolRepository {
    override fun getAllToolsStream(): Flow<List<ToolEntity>> =
        toolDao.selectAllTools()

    override fun getToolById(id: Long): Flow<ToolEntity?> =
        toolDao.selectToolById(id)

    override fun getToolByUsernameAndToolId(username: String, toolId: String): Flow<ToolEntity?> =
        toolDao.selectToolByUsernameAndToolId(username, toolId)

    override suspend fun saveTool(toolEntity: ToolEntity) =
        toolDao.insertTool(toolEntity)

    override suspend fun updateTool(toolEntity: ToolEntity) =
        toolDao.updateTool(toolEntity)

    override suspend fun removeTool(toolEntity: ToolEntity) =
        toolDao.deleteTool(toolEntity)
}