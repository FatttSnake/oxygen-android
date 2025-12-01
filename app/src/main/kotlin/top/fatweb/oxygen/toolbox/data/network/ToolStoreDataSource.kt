package top.fatweb.oxygen.toolbox.data.network

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.network.model.PageVo
import top.fatweb.oxygen.toolbox.network.model.Platform
import top.fatweb.oxygen.toolbox.network.model.ResponseResult
import top.fatweb.oxygen.toolbox.network.model.ToolBaseWithDistVo
import top.fatweb.oxygen.toolbox.network.model.ToolVo
import top.fatweb.oxygen.toolbox.network.model.ToolWithDistVo

interface ToolStoreDataSource {
    suspend fun getStore(
        searchValue: String = "",
        currentPage: Int = 1
    ): ResponseResult<PageVo<ToolVo>>

    fun getToolDist(
        username: String,
        toolId: String,
        ver: String = "latest",
        platform: Platform = Platform.Android
    ): Flow<Result<ToolWithDistVo>>

    fun getToolBaseDist(
        id: Long,
        version: Long
    ): Flow<Result<ToolBaseWithDistVo>>
}
