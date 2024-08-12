package top.fatweb.oxygen.toolbox.repository.tool

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity

interface StoreRepository {
    suspend fun getStore(searchValue: String, currentPage: Int): Flow<PagingData<ToolEntity>>

    fun detail(
        username: String,
        toolId: String,
        ver: String = "latest",
        platform: ToolEntity.Platform = ToolEntity.Platform.ANDROID
    ): Flow<Result<ToolEntity>>
}