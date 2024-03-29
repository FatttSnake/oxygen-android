package top.fatweb.oxygen.toolbox.repository.tool

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.tool.ToolGroup

interface ToolRepository {
    val toolGroups: Flow<List<ToolGroup>>
}