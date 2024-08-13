package top.fatweb.oxygen.toolbox.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.first
import top.fatweb.oxygen.toolbox.data.network.OxygenNetworkDataSource
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.network.model.ToolVo
import top.fatweb.oxygen.toolbox.network.model.asExternalModel

internal class ToolStorePagingSource(
    private val oxygenNetworkDataSource: OxygenNetworkDataSource,
    private val toolDao: ToolDao,
    private val searchValue: String
) : PagingSource<Int, ToolEntity>() {
    override fun getRefreshKey(state: PagingState<Int, ToolEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ToolEntity> {
        return try {
            val currentPage = params.key ?: 1
            val (_, success, msg, data) = oxygenNetworkDataSource.getStore(searchValue, currentPage)

            if (!success) {
                return LoadResult.Error(RuntimeException(msg))
            }
            val (_, pages, _, _, records) = data!!

            val nextPage = if (currentPage < pages) currentPage + 1 else null
            LoadResult.Page(
                data = records.map(ToolVo::asExternalModel).map { toolEntity ->
                    toolDao.selectToolByUsernameAndToolId(
                        toolEntity.authorUsername,
                        toolEntity.toolId
                    ).first()?.let {
                        if (it.id == toolEntity.id) {
                            it
                        } else {
                            it.copy(upgrade = toolEntity.ver).also { copy ->
                                toolDao.updateTool(copy)
                            }
                        }
                    } ?: toolEntity
                },
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

}