package top.fatweb.oxygen.toolbox.repository.tool.impl

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.data.tool.ToolDataSource
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject

class OfflineToolRepository @Inject constructor(
    private val toolDataSource: ToolDataSource,
    private val toolDao: ToolDao
) : ToolRepository {
    override val toolViewTemplate: Flow<String>
        get() = toolDataSource.toolViewTemplate

    override fun getAllToolsStream(searchValue: String): Flow<List<ToolEntity>> =
        toolDao.selectAllTools(searchValue)

    override fun getStarToolsStream(searchValue: String): Flow<List<ToolEntity>> =
        toolDao.selectStarTools(searchValue)

    override fun getToolById(id: Long): Flow<ToolEntity?> =
        toolDao.selectToolById(id)

    override fun getToolByUsernameAndToolId(username: String, toolId: String): Flow<ToolEntity?> =
        toolDao.selectToolByUsernameAndToolId(username, toolId)

    override suspend fun saveTool(toolEntity: ToolEntity) =
        toolDao.insertTool(toolEntity.copy(isInstalled = true))

    override suspend fun updateTool(toolEntity: ToolEntity) =
        toolDao.updateTool(toolEntity)

    override suspend fun removeTool(toolEntity: ToolEntity) =
        toolDao.deleteTool(toolEntity)
}
