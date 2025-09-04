package top.fatweb.oxygen.toolbox.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.first
import timber.log.Timber
import top.fatweb.oxygen.toolbox.data.network.ToolStoreDataSource
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.network.model.ToolVo
import top.fatweb.oxygen.toolbox.network.model.asExternalModel

internal class ToolStorePagingSource(
    private val toolStoreDataSource: ToolStoreDataSource,
    private val toolDao: ToolDao,
    private val searchValue: String
) : PagingSource<Int, ToolEntity>() {
    override fun getRefreshKey(state: PagingState<Int, ToolEntity>): Int? =
        state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ToolEntity> {
        return try {
            val currentPage = params.key ?: 1
            val (_, success, msg, data) = toolStoreDataSource.getStore(
                searchValue = searchValue,
                currentPage = currentPage
            )

            if (!success) {
                return LoadResult.Error(RuntimeException(msg))
            }
            val (_, pages, _, _, records) = data!!

            LoadResult.Page(
                data = records.map(ToolVo::asExternalModel).map { toolEntity ->
                    toolDao.selectByUsernameAndToolId(
                        username = toolEntity.authorUsername,
                        toolId = toolEntity.toolId
                    ).first()?.let {
                        if (it.id != toolEntity.id) {
                            it.copy(upgrade = toolEntity.ver).let { copy ->
                                toolDao.update(copy)
                            }
                        }
                        toolEntity.copy(installedVersion = it.ver)
                    } ?: toolEntity
                },
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage < pages) currentPage + 1 else null
            )
        } catch (e: Throwable) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }
}
