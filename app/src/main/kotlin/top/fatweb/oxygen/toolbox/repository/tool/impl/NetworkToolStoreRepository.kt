package top.fatweb.oxygen.toolbox.repository.tool.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import top.fatweb.oxygen.toolbox.data.network.ToolStoreDataSource
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.asExternalModel
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity
import top.fatweb.oxygen.toolbox.network.model.ToolBaseWithDistVo
import top.fatweb.oxygen.toolbox.network.model.ToolWithDistVo
import top.fatweb.oxygen.toolbox.network.model.asExternalModel
import top.fatweb.oxygen.toolbox.network.paging.ToolStorePagingSource
import top.fatweb.oxygen.toolbox.repository.tool.ToolStoreRepository
import javax.inject.Inject

private const val PAGE_SIZE = 20

internal class NetworkToolStoreRepository @Inject constructor(
    private val toolStoreDataSource: ToolStoreDataSource,
    private val toolDao: ToolDao
) : ToolStoreRepository {
    override suspend fun getStore(
        searchValue: String
    ): Flow<PagingData<ToolEntity>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                ToolStorePagingSource(
                    toolStoreDataSource = toolStoreDataSource,
                    toolDao = toolDao,
                    searchValue = searchValue
                )
            }
        ).flow

    override fun getToolDist(
        username: String,
        toolId: String,
        ver: String
    ): Flow<Result<ToolWithDistEntity>> =
        toolStoreDataSource.getToolDist(
            username = username,
            toolId = toolId,
            ver = ver
        ).map {
            it.asExternalModel(ToolWithDistVo::asExternalModel)
        }

    override fun getToolBaseDist(id: Long, version: Long): Flow<Result<ToolBaseWithDistEntity>> =
        toolStoreDataSource.getToolBaseDist(
            id = id,
            version = version
        ).map {
            it.asExternalModel(ToolBaseWithDistVo::asExternalModel)
        }
}
