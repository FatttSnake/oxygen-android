package top.fatweb.oxygen.toolbox.ui.tool

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import top.fatweb.oxygen.toolbox.ui.component.ToolGroupCard

fun LazyStaggeredGridScope.toolsPanel(
    toolsScreenUiState: ToolsScreenUiState
) {
    when (toolsScreenUiState) {
        ToolsScreenUiState.Loading -> Unit

        is ToolsScreenUiState.Success -> {
            items(
                items = toolsScreenUiState.toolGroups,
                key = { it.id },
            ) {
                ToolGroupCard(toolGroup = it)
            }
        }
    }
}