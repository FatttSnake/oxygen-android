package top.fatweb.oxygen.toolbox.ui.star

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.ui.component.DialogClickerRow
import top.fatweb.oxygen.toolbox.ui.component.DialogSectionGroup
import top.fatweb.oxygen.toolbox.ui.component.DialogTitle
import top.fatweb.oxygen.toolbox.ui.component.Indicator
import top.fatweb.oxygen.toolbox.ui.component.ToolCard
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.scrollbarState

@Composable
internal fun StarRoute(
    modifier: Modifier = Modifier,
    viewModel: StarScreenViewModel = hiltViewModel(),
    searchValue: String,
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit
) {
    val starScreenUiState by viewModel.starScreenUiState.collectAsStateWithLifecycle()

    LaunchedEffect(searchValue) {
        viewModel.onSearchValueChange(searchValue)
    }

    StarScreen(
        modifier = modifier,
        starScreenUiState = starScreenUiState,
        onNavigateToToolView = onNavigateToToolView,
        onUnstar = viewModel::unstar
    )
}

@Composable
internal fun StarScreen(
    modifier: Modifier = Modifier,
    starScreenUiState: StarScreenUiState,
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit,
    onUnstar: (ToolEntity) -> Unit
) {
    ReportDrawnWhen { starScreenUiState !is StarScreenUiState.Loading }

    val itemsAvailable = howManyTools(starScreenUiState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)

    var selectedTool by remember { mutableStateOf<ToolEntity?>(null) }
    var isShowMenu by remember { mutableStateOf(true) }

    Box(
        modifier.fillMaxSize()
    ) {
        when (starScreenUiState) {
            StarScreenUiState.Loading -> {
                Indicator()
            }

            StarScreenUiState.Nothing -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.feature_star_no_tools_starred))
                }
            }

            is StarScreenUiState.Success -> {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = StaggeredGridCells.Adaptive(160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 24.dp,
                    state = state
                ) {
                    toolsPanel(
                        toolItems = starScreenUiState.tools,
                        onClick = onNavigateToToolView,
                        onLongClick = {
                            selectedTool = it
                            isShowMenu = true
                        }
                    )

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(Modifier.height(8.dp))
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
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(itemsAvailable = itemsAvailable)
        )
    }

    if (isShowMenu && selectedTool != null) {
        ToolMenu(
            selectedTool = selectedTool!!,
            onDismiss = { isShowMenu = false },
            onUnstar = {
                isShowMenu = false
                onUnstar(selectedTool!!)
            }
        )
    }
}

private fun LazyStaggeredGridScope.toolsPanel(
    toolItems: List<ToolEntity>,
    onClick: (username: String, toolId: String, preview: Boolean) -> Unit,
    onLongClick: (ToolEntity) -> Unit
) {
    items(
        items = toolItems,
        key = { it.id }
    ) {
        ToolCard(
            tool = it,
            actionIcon = if (it.isStar) OxygenIcons.Star else null,
            actionIconContentDescription = stringResource(R.string.core_star),
            onClick = { onClick(it.authorUsername, it.toolId, false) },
            onLongClick = { onLongClick(it) },
            onAction = { onClick(it.authorUsername, it.toolId, false) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolMenu(
    modifier: Modifier = Modifier,
    selectedTool: ToolEntity,
    onDismiss: () -> Unit,
    onUnstar: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, dragHandle = {}) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            DialogTitle(text = selectedTool.name)
            HorizontalDivider()
            Spacer(Modifier.height(4.dp))
            DialogSectionGroup {
                DialogClickerRow(
                    icon = OxygenIcons.StarBorder,
                    text = stringResource(R.string.core_unstar),
                    onClick = onUnstar
                )
            }
        }
    }
}

@Composable
private fun howManyTools(starScreenUiState: StarScreenUiState) =
    when (starScreenUiState) {
        StarScreenUiState.Loading, StarScreenUiState.Nothing -> 0
        is StarScreenUiState.Success -> starScreenUiState.tools.size
    }
