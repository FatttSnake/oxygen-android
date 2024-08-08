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
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.Loading
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.ui.component.ToolCard
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.scrollbarState

@Composable
internal fun ToolsRoute(
    modifier: Modifier = Modifier,
    viewModel: ToolsScreenViewModel = hiltViewModel(),
    onNavigateToToolView: (username: String, toolId: String) -> Unit,
    onNavigateToToolStore: () -> Unit
) {
    val toolsScreenUiStateState by viewModel.toolsScreenUiState.collectAsStateWithLifecycle()

    ToolsScreen(
        modifier = modifier,
        onNavigateToToolView = onNavigateToToolView,
        onNavigateToToolStore = onNavigateToToolStore,
        toolsScreenUiState = toolsScreenUiStateState
    )
}

@Composable
internal fun ToolsScreen(
    modifier: Modifier = Modifier,
    onNavigateToToolView: (username: String, toolId: String) -> Unit,
    onNavigateToToolStore: () -> Unit,
    toolsScreenUiState: ToolsScreenUiState
) {
    ReportDrawnWhen { toolsScreenUiState !is ToolsScreenUiState.Loading }

    val itemsAvailable = howManyTools(toolsScreenUiState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    Box(
        modifier.fillMaxSize()
    ) {

        when (toolsScreenUiState) {
            ToolsScreenUiState.Loading -> {
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
            ToolsScreenUiState.Nothing -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.feature_tools_no_tools_installed))
                    TextButton(onClick = onNavigateToToolStore) {
                        Text(text = stringResource(R.string.feature_tools_go_to_store))
                    }
                }
            }
            is ToolsScreenUiState.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 24.dp,
                    state = state
                ) {

                    toolsPanel(
                        toolItems = toolsScreenUiState.tools,
                        onClickToolCard = onNavigateToToolView
                    )

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                    }
                }
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

private fun LazyStaggeredGridScope.toolsPanel(
    toolItems: List<ToolEntity>,
    onClickToolCard: (username: String, toolId: String) -> Unit
) {
    items(
        items = toolItems,
        key = { it.id },
    ) {
        ToolCard(
            tool = it,
            onClick = {onClickToolCard(it.authorUsername, it.toolId)},
            onLongClick = {onClickToolCard(it.authorUsername, it.toolId)}
        )
    }
}

@Composable
private fun howManyTools(toolsScreenUiState: ToolsScreenUiState) =
    when (toolsScreenUiState) {
        ToolsScreenUiState.Loading, ToolsScreenUiState.Nothing -> 0
        is ToolsScreenUiState.Success -> toolsScreenUiState.tools.size
    }
