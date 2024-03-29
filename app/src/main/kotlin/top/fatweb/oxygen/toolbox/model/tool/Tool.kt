package top.fatweb.oxygen.toolbox.model.tool

import androidx.compose.ui.graphics.vector.ImageVector
import top.fatweb.oxygen.toolbox.icon.OxygenIcons

data class Tool(
    val id: String,

    val icon: ImageVector = OxygenIcons.Tool,

    val name: String
)