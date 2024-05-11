package top.fatweb.oxygen.toolbox.repository.tool.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import top.fatweb.oxygen.toolbox.data.network.OxygenNetworkDataSource
import top.fatweb.oxygen.toolbox.data.tool.ToolDataSource
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.asExternalModel
import top.fatweb.oxygen.toolbox.model.tool.Tool
import top.fatweb.oxygen.toolbox.network.model.ToolBaseVo
import top.fatweb.oxygen.toolbox.network.model.ToolVo
import top.fatweb.oxygen.toolbox.network.model.asExternalModel
import top.fatweb.oxygen.toolbox.network.paging.ToolStorePagingSource
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject

private const val PAGE_SIZE = 20

internal class NetworkToolRepository @Inject constructor(
    private val oxygenNetworkDataSource: OxygenNetworkDataSource,
    private val toolDataSource: ToolDataSource
) : ToolRepository {
    override val toolViewTemplate: Flow<String>
        get() = toolDataSource.toolViewTemplate

    override suspend fun getStore(searchValue: String, currentPage: Int): Flow<PagingData<Tool>> =
        Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { ToolStorePagingSource(oxygenNetworkDataSource, searchValue) }
        ).flow

    override fun detail(
        username: String,
        toolId: String,
        ver: String,
        platform: Tool.Platform
    ): Flow<Result<Tool>> =
        oxygenNetworkDataSource.detail(
            username,
            toolId,
            ver,
            ToolBaseVo.Platform.valueOf(platform.name)
        ).map {
            it.asExternalModel(ToolVo::asExternalModel)
        }
}