package top.fatweb.oxygen.toolbox.repository.tool.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import top.fatweb.oxygen.toolbox.data.tool.ToolDataSource
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolBaseDao
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity
import top.fatweb.oxygen.toolbox.repository.storage.CASRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject

class OfflineToolRepository @Inject constructor(
    private val toolDataSource: ToolDataSource,
    private val toolDao: ToolDao,
    private val toolBaseDao: ToolBaseDao,
    private val casRepository: CASRepository
) : ToolRepository {
    override val toolViewTemplate: Flow<String>
        get() = toolDataSource.toolViewTemplate

    override fun getGlobalJsVariables(isDarkMode: Boolean): Flow<String> =
        toolDataSource.getGlobalJsVariables(isDarkMode)

    override fun getGlobalCssVariables(isDarkMode: Boolean): Flow<String> =
        toolDataSource.getGlobalCssVariables(isDarkMode)

    override fun getAllToolsStream(searchValue: String): Flow<List<ToolWithDistEntity>> =
        toolDao.selectAll(searchValue).map { list -> list.map { it.resolveDist() } }

    override fun getStarToolsStream(searchValue: String): Flow<List<ToolWithDistEntity>> =
        toolDao.selectStar(searchValue).map { list -> list.map { it.resolveDist() } }

    override fun getToolById(id: Long): Flow<ToolWithDistEntity?> =
        toolDao.selectById(id).map { it?.resolveDist() }

    override fun getToolByUsernameAndToolId(
        username: String,
        toolId: String
    ): Flow<ToolWithDistEntity?> =
        toolDao.selectByUsernameAndToolId(username, toolId).map { it?.resolveDist() }

    override suspend fun saveTool(toolWithDistEntity: ToolWithDistEntity) {
        val hash = casRepository.save(toolWithDistEntity.dist)
        toolDao.insert(toolWithDistEntity.copy(dist = hash))
    }

    override suspend fun updateTool(toolWithDistEntity: ToolWithDistEntity) {
        val hash = casRepository.save(toolWithDistEntity.dist)
        toolDao.update(toolWithDistEntity.copy(dist = hash))
    }

    override suspend fun removeTool(toolWithDistEntity: ToolWithDistEntity) {
        val tool = toolDao.selectById(toolWithDistEntity.id).first() ?: return
        toolDao.deleteTool(toolWithDistEntity)
        tryDeleteDist(tool.dist)
        tryMarkToolBaseAsCache(
            baseId = toolWithDistEntity.baseId,
            baseVersion = toolWithDistEntity.baseVersion
        )
    }

    override suspend fun removeTool(username: String, toolId: String) {
        val tool = toolDao.selectByUsernameAndToolId(username, toolId).first() ?: return
        toolDao.deleteTool(username = username, toolId = toolId)
        tryDeleteDist(tool.dist)
        tryMarkToolBaseAsCache(
            baseId = tool.baseId,
            baseVersion = tool.baseVersion
        )
    }

    override fun getToolBaseByIdAndVersion(id: Long, version: Long): Flow<ToolBaseWithDistEntity?> =
        toolBaseDao.selectByIdAndVersion(id = id, version = version).map { it?.resolveDist() }

    override suspend fun saveToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity) {
        val hash = casRepository.save(toolBaseWithDistEntity.dist)
        toolBaseDao.insert(toolBaseWithDistEntity.copy(dist = hash))
    }

    override suspend fun updateToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity) {
        val hash = casRepository.save(toolBaseWithDistEntity.dist)
        toolBaseDao.update(toolBaseWithDistEntity.copy(dist = hash))
    }

    override suspend fun removeToolBase(toolBaseWithDistEntity: ToolBaseWithDistEntity) =
        toolBaseDao.delete(toolBaseWithDistEntity)

    /**
     * If no installed tool references this toolBase, mark it as cache so it can
     * be cleaned up the next time [clearToolBaseCache] is called.
     */
    private suspend fun tryMarkToolBaseAsCache(baseId: Long, baseVersion: Long) {
        if (toolDao.countByBaseIdAndVersion(baseId = baseId, baseVersion = baseVersion) == 0L) {
            toolBaseDao.markAsCache(id = baseId, version = baseVersion)
        }
    }

    override suspend fun clearToolBaseCache() {
        val cachedHashes = toolBaseDao.selectCachedDistHashes()
        toolBaseDao.clearCache()
        cachedHashes.forEach { tryDeleteDist(it) }
    }

    /**
     * Delete a dist file from CAS storage if no remaining entity references it.
     *
     * Since CAS is content-addressed (SHA-256), multiple tools or tool bases may share
     * the same hash. We only delete the file when both the [ToolDao] and [ToolBaseDao]
     * report zero references.
     */
    private suspend fun tryDeleteDist(hash: String) {
        if (toolDao.countByDistHash(hash) == 0L && toolBaseDao.countByDistHash(hash) == 0L) {
            casRepository.delete(hash)
        }
    }

    /**
     * Resolve the actual dist content from file storage.
     *
     * The `dist` column in Room stores only a 64-character SHA-256 file key.
     * This function loads the actual content from [CASRepository] and replaces
     * the key with the real content. If the file does not exist (e.g., legacy data
     * after migration), the entity is returned as-is with whatever is in the
     * `dist` column.
     */
    private fun ToolWithDistEntity.resolveDist(): ToolWithDistEntity {
        val content = casRepository.load(dist)?.toString(Charsets.UTF_8)
        return if (content != null) copy(dist = content) else this
    }

    /**
     * Resolve the actual tool base dist content from file storage.
     *
     * Same pattern as [ToolWithDistEntity.resolveDist] for tool base entities.
     */
    private fun ToolBaseWithDistEntity.resolveDist(): ToolBaseWithDistEntity {
        val content = casRepository.load(dist)?.toString(Charsets.UTF_8)
        return if (content != null) copy(dist = content) else this
    }
}
