package top.fatweb.oxygen.toolbox.model.tool

import androidx.compose.ui.graphics.vector.ImageVector
import top.fatweb.oxygen.toolbox.icon.OxygenIcons

data class ToolGroup(
    val id: String,

    val icon: ImageVector = OxygenIcons.Box,

    val title: String,

    val tools: List<ToolEntity> = emptyList()
)
