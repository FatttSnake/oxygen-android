package top.fatweb.oxygen.toolbox.ui.tool

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.datastore.tool.ToolDataSource
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.scrollbarState

@Composable
fun ToolsRoute(
    modifier: Modifier = Modifier,
    viewModel: ToolsScreenViewModel = hiltViewModel()
) {
    val toolsScreenUiState by viewModel.toolsScreenUiState.collectAsStateWithLifecycle()

    ToolsScreen(
        modifier = modifier,
        toolsScreenUiState = toolsScreenUiState
    )
}

@Composable
fun ToolsScreen(
    modifier: Modifier = Modifier,
    toolsScreenUiState: ToolsScreenUiState
) {
    val isToolLoading = toolsScreenUiState is ToolsScreenUiState.Loading

    ReportDrawnWhen { !isToolLoading }

    val itemsAvailable = howManyItems(toolsScreenUiState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)

    var isExpanded by remember { mutableStateOf(mapOf<String, Boolean>()) }

    Box(
        modifier.fillMaxSize()
    ) {
        when (toolsScreenUiState) {
            ToolsScreenUiState.Loading -> {
                Text(text = stringResource(R.string.feature_settings_loading))
            }

            is ToolsScreenUiState.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(300.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 24.dp,
                    state = state
                ) {

                    toolsPanel(toolsScreenUiState = toolsScreenUiState)

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
    }
}

fun howManyItems(toolScreenUiState: ToolsScreenUiState) =
    when (toolScreenUiState) {
        ToolsScreenUiState.Loading -> 0

        is ToolsScreenUiState.Success -> toolScreenUiState.toolGroups.size
    }

@Preview("Loading")
@Composable
fun ToolsScreenLoadingPreview() {
    ToolsScreen(toolsScreenUiState = ToolsScreenUiState.Loading)
}

@Preview("ToolsPage")
@Composable
fun ToolsScreenPreview() {
    ToolsScreen(
        toolsScreenUiState = ToolsScreenUiState.Success(
            runBlocking {
                ToolDataSource().tool.first()
            })
    )
}