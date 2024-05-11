package top.fatweb.oxygen.toolbox.ui.tool

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import top.fatweb.oxygen.toolbox.icon.Loading
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.tool.Tool
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.scrollbarState

@Composable
internal fun ToolsRoute(
    modifier: Modifier = Modifier,
    viewModel: ToolsScreenViewModel = hiltViewModel(),
    onNavigateToToolView: (username: String, toolId: String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    handleOnCanScrollChange: (Boolean) -> Unit
) {
    val toolStorePagingItems = viewModel.storeData.collectAsLazyPagingItems()

    ToolsScreen(
        modifier = modifier,
        onNavigateToToolView = onNavigateToToolView,
        onShowSnackbar = onShowSnackbar,
        handleOnCanScrollChange = handleOnCanScrollChange,
        toolStorePagingItems = toolStorePagingItems
    )
}

@Composable
internal fun ToolsScreen(
    modifier: Modifier = Modifier,
    onNavigateToToolView: (username: String, toolId: String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    handleOnCanScrollChange: (Boolean) -> Unit,
    toolStorePagingItems: LazyPagingItems<Tool>
) {
    val isToolLoading =
        toolStorePagingItems.loadState.refresh is LoadState.Loading
                || toolStorePagingItems.loadState.append is LoadState.Loading

    ReportDrawnWhen { !isToolLoading }

    val itemsAvailable = toolStorePagingItems.itemCount

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    LaunchedEffect(state.canScrollForward) {
        handleOnCanScrollChange(state.canScrollForward)
    }
    Box(
        modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            state = state
        ) {

            toolsPanel(
                toolStorePagingItems = toolStorePagingItems,
                onClickToolCard = onNavigateToToolView
            )

            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(8.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }

        if (toolStorePagingItems.loadState.refresh is LoadState.Loading || toolStorePagingItems.loadState.append is LoadState.Loading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val angle by infiniteTransition.animateFloat(
                    initialValue = 0F,
                    targetValue = 360F,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = Ease),
                    ), label = "angle"
                )
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer { rotationZ = angle },
                    imageVector = OxygenIcons.Loading,
                    contentDescription = ""
                )
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