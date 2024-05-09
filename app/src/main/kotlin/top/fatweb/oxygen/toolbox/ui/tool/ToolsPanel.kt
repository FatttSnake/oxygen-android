package top.fatweb.oxygen.toolbox.ui.tool

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.paging.compose.LazyPagingItems
import top.fatweb.oxygen.toolbox.model.tool.Tool

fun LazyStaggeredGridScope.toolsPanel(
    toolStorePagingItems: LazyPagingItems<Tool>
) {
    items(
        items = toolStorePagingItems.itemSnapshotList,
        key = { it!!.id },
    ) {
        Text(text = it!!.name)
    }
}