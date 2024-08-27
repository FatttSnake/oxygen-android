package top.fatweb.oxygen.toolbox.ui.tools

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.ui.component.DEFAULT_TOOL_CARD_SKELETON_COUNT
import top.fatweb.oxygen.toolbox.ui.component.DialogClickerRow
import top.fatweb.oxygen.toolbox.ui.component.DialogSectionGroup
import top.fatweb.oxygen.toolbox.ui.component.DialogTitle
import top.fatweb.oxygen.toolbox.ui.component.Indicator
import top.fatweb.oxygen.toolbox.ui.component.ToolCard
import top.fatweb.oxygen.toolbox.ui.component.ToolCardSkeleton
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.scrollbarState
import top.fatweb.oxygen.toolbox.ui.util.ResourcesUtils

@Composable
internal fun ToolsRoute(
    modifier: Modifier = Modifier,
    viewModel: ToolsScreenViewModel = hiltViewModel(),
    searchValue: String,
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit,
    onNavigateToToolStore: () -> Unit,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    val toolsScreenUiStateState by viewModel.toolsScreenUiState.collectAsStateWithLifecycle()

    LaunchedEffect(searchValue) {
        viewModel.onSearchValueChange(searchValue)
    }

    ToolsScreen(
        modifier = modifier,
        toolsScreenUiState = toolsScreenUiStateState,
        onNavigateToToolView = onNavigateToToolView,
        onNavigateToToolStore = onNavigateToToolStore,
        onShowSnackbar = onShowSnackbar,
        onUninstall = viewModel::uninstall,
        onUndo = viewModel::undo,
        onChangeStar = viewModel::changeStar
    )
}

@Composable
internal fun ToolsScreen(
    modifier: Modifier = Modifier,
    toolsScreenUiState: ToolsScreenUiState,
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit,
    onNavigateToToolStore: () -> Unit,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean,
    onUninstall: (ToolEntity) -> Unit,
    onUndo: (ToolEntity) -> Unit,
    onChangeStar: (ToolEntity, Boolean) -> Unit
) {
    val localContext = LocalContext.current

    val scope = rememberCoroutineScope()

    ReportDrawnWhen { toolsScreenUiState !is ToolsScreenUiState.Loading }

    val itemsAvailable = howManyTools(toolsScreenUiState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)

    var selectedTool by remember { mutableStateOf<ToolEntity?>(null) }
    var isShowMenu by remember { mutableStateOf(true) }

    Box(
        modifier.fillMaxSize()
    ) {

        when (toolsScreenUiState) {
            ToolsScreenUiState.Loading -> {
                Indicator()
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = StaggeredGridCells.Adaptive(160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 24.dp,
                    state = state
                ) {
                    items(count = DEFAULT_TOOL_CARD_SKELETON_COUNT) {
                        ToolCardSkeleton()
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(Modifier.height(8.dp))
                        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                    }
                }
            }

            ToolsScreenUiState.Nothing -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = rememberScrollState()),
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
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = StaggeredGridCells.Adaptive(160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 24.dp,
                    state = state
                ) {

                    toolsPanel(
                        toolItems = toolsScreenUiState.tools,
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
            onDismiss = { isShowMenu = false },
            selectedTool = selectedTool!!,
            onUninstall = {
                isShowMenu = false
                onUninstall(selectedTool!!)
                scope.launch {
                    if (onShowSnackbar(
                            ResourcesUtils.getString(localContext, R.string.core_uninstall_success),
                            ResourcesUtils.getString(localContext, R.string.core_undo)
                        )
                    ) {
                        onUndo(selectedTool!!)
                    }
                }
            },
            onChangeStar = {
                isShowMenu = false
                onChangeStar(selectedTool!!, it)
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
    onDismiss: () -> Unit,
    selectedTool: ToolEntity,
    onUninstall: () -> Unit,
    onChangeStar: (Boolean) -> Unit
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
                    icon = OxygenIcons.Delete,
                    text = stringResource(R.string.core_uninstall),
                    onClick = onUninstall
                )
                DialogClickerRow(
                    icon = if (selectedTool.isStar) OxygenIcons.StarBorder else OxygenIcons.Star,
                    text = stringResource(if (selectedTool.isStar) R.string.core_unstar else R.string.core_star),
                    onClick = { onChangeStar(!selectedTool.isStar) }
                )
            }
        }
    }
}


@Composable
private fun howManyTools(toolsScreenUiState: ToolsScreenUiState) =
    when (toolsScreenUiState) {
        ToolsScreenUiState.Loading -> DEFAULT_TOOL_CARD_SKELETON_COUNT
        ToolsScreenUiState.Nothing -> 0
        is ToolsScreenUiState.Success -> toolsScreenUiState.tools.size
    }
