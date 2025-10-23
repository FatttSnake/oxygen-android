package top.fatweb.oxygen.toolbox.repository.tool.impl

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.data.tool.ToolDataSource
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolBaseDao
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject

class OfflineToolRepository @Inject constructor(
    private val toolDataSource: ToolDataSource,
    private val toolDao: ToolDao,
    private val toolBaseDao: ToolBaseDao
) : ToolRepository {
    override val toolViewTemplate: Flow<String>
        get() = toolDataSource.toolViewTemplate

    override fun getGlobalJsVariables(isDarkMode: Boolean): Flow<String> =
        toolDataSource.getGlobalJsVariables(isDarkMode)

    override fun getGlobalCssVariables(isDarkMode: Boolean): Flow<String> =
        toolDataSource.getGlobalCssVariables(isDarkMode)

    override fun getAllToolsStream(searchValue: String): Flow<List<ToolWithDistEntity>> =
        toolDao.selectAll(searchValue)

    override fun getStarToolsStream(searchValue: String): Flow<List<ToolWithDistEntity>> =
        toolDao.selectStar(searchValue)

    override fun getToolById(id: Long): Flow<ToolWithDistEntity?> =
        toolDao.selectById(id)

    override fun getToolByUsernameAndToolId(
        username: String,
        toolId: String
    ): Flow<ToolWithDistEntity?> =
        toolDao.selectByUsernameAndToolId(username, toolId)

    override suspend fun saveTool(toolWithDistEntity: ToolWithDistEntity) =
        toolDao.insert(toolWithDistEntity)

    override suspend fun updateTool(toolWithDistEntity: ToolWithDistEntity) =
        toolDao.update(toolWithDistEntity)

    override suspend fun removeTool(toolWithDistEntity: ToolWithDistEntity) =
        toolDao.deleteTool(toolWithDistEntity)

    override suspend fun removeTool(username: String, toolId: String) =
        toolDao.deleteTool(username = username, toolId = toolId)

    override fun getToolBaseByIdAndVersion(id: Long, version: Long): Flow<ToolBaseWithDistEntity?> =
        toolBaseDao.selectByIdAndVersion(id = id, version = version)

    override suspend fun saveToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity) =
        toolBaseDao.insert(toolBaseWithDistEntity)

    override suspend fun updateToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity) =
        toolBaseDao.update(toolBaseWithDistEntity)

    override suspend fun removeToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity) =
        toolBaseDao.delete(toolBaseWithDistEntity)

    override suspend fun clearToolBaseCache() =
        toolBaseDao.clearCache()
}
