package top.fatweb.oxygen.toolbox.repository.tool

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.tool.Tool

interface ToolRepository {
    val toolViewTemplate: Flow<String>

    suspend fun getStore(searchValue: String, currentPage: Int): Flow<PagingData<Tool>>

    fun detail(
        username: String,
        toolId: String,
        ver: String = "latest",
        platform: Tool.Platform = Tool.Platform.ANDROID
    ): Flow<Result<Tool>>
}