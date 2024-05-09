package top.fatweb.oxygen.toolbox.ui.tool

import android.util.Log
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import top.fatweb.oxygen.toolbox.model.tool.Tool
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.scrollbarState

@Composable
internal fun ToolsRoute(
    modifier: Modifier = Modifier,
    viewModel: ToolsScreenViewModel = hiltViewModel()
) {
    val toolStorePagingItems = viewModel.getStoreData().collectAsLazyPagingItems()

    ToolsScreen(
        modifier = modifier,
        toolStorePagingItems = toolStorePagingItems
    )
}

@Composable
internal fun ToolsScreen(
    modifier: Modifier = Modifier,
    toolStorePagingItems: LazyPagingItems<Tool>
) {
    val isToolLoading = toolStorePagingItems.loadState.refresh is LoadState.Loading

    Log.d("TAG", "ToolsScreen: ${toolStorePagingItems.loadState}")
    
    ReportDrawnWhen { !isToolLoading }

    val itemsAvailable = toolStorePagingItems.itemCount

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)

    Box(
        modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            state = state
        ) {

            toolsPanel(toolStorePagingItems = toolStorePagingItems)

            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(8.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }

        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState, orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(itemsAvailable = itemsAvailable)
        )
    }
}

/*
@OxygenPreviews
@Composable
fun ToolsScreenLoadingPreview() {
    OxygenTheme {
        ToolsScreen(toolsScreenUiState = ToolsScreenUiState.Loading)
    }
}

@OxygenPreviews
@Composable
fun ToolsScreenPreview() {
    OxygenTheme {
        ToolsScreen(
            toolsScreenUiState = ToolsScreenUiState.Success(
                runBlocking {
                    ToolDataSource().tool.first()
                })
        )
    }
}*/
