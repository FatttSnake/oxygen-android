package top.fatweb.oxygen.toolbox.repository.tool.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import top.fatweb.oxygen.toolbox.data.network.OxygenNetworkDataSource
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.asExternalModel
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.network.model.ToolBaseVo
import top.fatweb.oxygen.toolbox.network.model.ToolVo
import top.fatweb.oxygen.toolbox.network.model.asExternalModel
import top.fatweb.oxygen.toolbox.network.paging.ToolStorePagingSource
import top.fatweb.oxygen.toolbox.repository.tool.StoreRepository
import javax.inject.Inject

private const val PAGE_SIZE = 20

internal class NetworkStoreRepository @Inject constructor(
    private val oxygenNetworkDataSource: OxygenNetworkDataSource,
    private val toolDao: ToolDao
) : StoreRepository {

    override suspend fun getStore(
        searchValue: String,
        currentPage: Int
    ): Flow<PagingData<ToolEntity>> =
        Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = {
                ToolStorePagingSource(
                    oxygenNetworkDataSource,
                    toolDao,
                    searchValue
                )
            }
        ).flow

    override fun detail(
        username: String,
        toolId: String,
        ver: String,
        platform: ToolEntity.Platform
    ): Flow<Result<ToolEntity>> =
        oxygenNetworkDataSource.detail(
            username,
            toolId,
            ver,
            ToolBaseVo.Platform.valueOf(platform.name)
        ).map {
            it.asExternalModel(ToolVo::asExternalModel)
        }
}