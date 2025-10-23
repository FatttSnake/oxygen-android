package top.fatweb.oxygen.toolbox.repository.tool

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity

interface ToolStoreRepository {
    suspend fun getStore(
        searchValue: String
    ): Flow<PagingData<ToolEntity>>

    fun getToolDist(
        username: String,
        toolId: String,
        ver: String = "latest"
    ): Flow<Result<ToolWithDistEntity>>

    fun getToolBaseDist(
        id: Long,
        version: Long
    ): Flow<Result<ToolBaseWithDistEntity>>
}
