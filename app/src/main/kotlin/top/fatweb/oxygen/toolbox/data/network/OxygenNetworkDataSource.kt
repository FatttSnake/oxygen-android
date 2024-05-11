package top.fatweb.oxygen.toolbox.data.network

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.network.model.PageVo
import top.fatweb.oxygen.toolbox.network.model.ResponseResult
import top.fatweb.oxygen.toolbox.network.model.ToolBaseVo
import top.fatweb.oxygen.toolbox.network.model.ToolVo

interface OxygenNetworkDataSource {
    suspend fun getStore(
        searchValue: String = "",
        currentPage: Int = 1
    ): ResponseResult<PageVo<ToolVo>>

    fun detail(
        username: String,
        toolId: String,
        ver: String = "latest",
        platform: ToolBaseVo.Platform = ToolBaseVo.Platform.ANDROID
    ): Flow<Result<ToolVo>>
}