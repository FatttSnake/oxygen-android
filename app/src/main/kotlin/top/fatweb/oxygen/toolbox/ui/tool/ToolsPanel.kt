package top.fatweb.oxygen.toolbox.ui.tool

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.paging.compose.LazyPagingItems
import top.fatweb.oxygen.toolbox.model.tool.Tool
import top.fatweb.oxygen.toolbox.ui.component.ToolCard

fun LazyStaggeredGridScope.toolsPanel(
    toolStorePagingItems: LazyPagingItems<Tool>,
    onClickToolCard: (username: String, toolId: String) -> Unit
) {
    items(
        items = toolStorePagingItems.itemSnapshotList,
        key = { it!!.id },
    ) {
        ToolCard(
            tool = it!!,
            onClickToolCard = {onClickToolCard(it.author.username, it.toolId)}
        )
    }
}