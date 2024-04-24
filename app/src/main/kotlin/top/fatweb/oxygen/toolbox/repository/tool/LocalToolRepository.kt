package top.fatweb.oxygen.toolbox.repository.tool

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.data.tool.ToolDataSource
import top.fatweb.oxygen.toolbox.model.tool.ToolGroup
import javax.inject.Inject

internal class LocalToolRepository @Inject constructor(
    toolDataSource: ToolDataSource
) : ToolRepository {
    override val toolGroups: Flow<List<ToolGroup>> =
        toolDataSource.tool
}