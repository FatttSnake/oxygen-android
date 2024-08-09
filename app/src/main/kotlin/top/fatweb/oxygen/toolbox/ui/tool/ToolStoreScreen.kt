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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.StateFlow
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.Loading
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.ui.component.ToolCard
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.scrollbarState

@Composable
internal fun ToolStoreRoute(
    modifier: Modifier = Modifier,
    viewModel: ToolStoreViewModel = hiltViewModel(),
    onNavigateToToolView: (username: String, toolId: String) -> Unit,
) {
    val toolStorePagingItems = viewModel.storeData.collectAsLazyPagingItems()
    val installInfo by viewModel.installInfo.collectAsState()

    ToolStoreScreen(
        modifier = modifier,
        onNavigateToToolView = onNavigateToToolView,
        toolStorePagingItems = toolStorePagingItems,
        hasInstalled = { viewModel.hasInstalled(it) },
        onChangeInstallStatus = viewModel::changeInstallStatus,
        onInstallTool = viewModel::installTool,
        installInfo = installInfo
    )
}

@Composable
internal fun ToolStoreScreen(
    modifier: Modifier = Modifier,
    onNavigateToToolView: (username: String, toolId: String) -> Unit,
    toolStorePagingItems: LazyPagingItems<ToolEntity>,
    hasInstalled: (ToolEntity) -> StateFlow<Boolean>,
    onChangeInstallStatus: (installStatus: ToolStoreUiState.Status, username: String?, toolId: String?) -> Unit,
    onInstallTool: () -> Unit,
    installInfo: ToolStoreUiState.InstallInfo
) {
    val isToolLoading =
        toolStorePagingItems.loadState.refresh is LoadState.Loading
                || toolStorePagingItems.loadState.append is LoadState.Loading

    ReportDrawnWhen { !isToolLoading }

    val itemsAvailable = toolStorePagingItems.itemCount

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

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
                hasInstalled = hasInstalled,
                onAction = { username, toolId ->
                    onChangeInstallStatus(
                        ToolStoreUiState.Status.Pending,
                        username,
                        toolId
                    )
                },
                onClick = onNavigateToToolView
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

    if (installInfo.status != ToolStoreUiState.Status.None) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AlertDialog(
                onDismissRequest = {
                    if (installInfo.status == ToolStoreUiState.Status.Pending) {
                        onChangeInstallStatus(ToolStoreUiState.Status.None, null, null)
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (installInfo.status) {
                                ToolStoreUiState.Status.Success -> OxygenIcons.Success
                                ToolStoreUiState.Status.Fail -> OxygenIcons.Error
                                else -> OxygenIcons.Info
                            },
                            contentDescription = stringResource(R.string.core_install)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(
                                when (installInfo.status) {
                                    ToolStoreUiState.Status.Success -> R.string.feature_store_install_success
                                    ToolStoreUiState.Status.Fail -> R.string.feature_store_install_fail
                                    else -> R.string.feature_store_install_tool
                                }
                            )
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .width(360.dp)
                            .padding(vertical = 16.dp)
                    ) {
                        when (installInfo.status) {
                            ToolStoreUiState.Status.Pending ->
                                Text(
                                    text = stringResource(
                                        R.string.feature_store_ask_install,
                                        installInfo.username,
                                        installInfo.toolId
                                    )
                                )

                            ToolStoreUiState.Status.Installing ->
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(text = stringResource(R.string.core_installing))
                                }

                            ToolStoreUiState.Status.Success ->
                                Text(text = stringResource(R.string.feature_store_install_success_info))

                            ToolStoreUiState.Status.Fail ->
                                Text(text = stringResource(R.string.feature_store_install_fail_info))

                            ToolStoreUiState.Status.None -> Unit
                        }
                    }
                },
                dismissButton = {
                    if (installInfo.status == ToolStoreUiState.Status.Pending) {
                        TextButton(onClick = {
                            onChangeInstallStatus(ToolStoreUiState.Status.None, null, null)
                        }) {
                            Text(text = stringResource(R.string.core_cancel))
                        }
                    }
                },
                confirmButton = {
                    when (installInfo.status) {
                        ToolStoreUiState.Status.Pending ->
                            TextButton(onClick = onInstallTool) {
                                Text(text = stringResource(R.string.core_install))
                            }

                        ToolStoreUiState.Status.Success,
                        ToolStoreUiState.Status.Fail ->
                            TextButton(onClick = {
                                onChangeInstallStatus(ToolStoreUiState.Status.None, null, null)
                            }) {
                                Text(
                                    text = stringResource(
                                        if (installInfo.status == ToolStoreUiState.Status.Success) R.string.core_ok
                                        else R.string.core_close
                                    )
                                )
                            }

                        ToolStoreUiState.Status.None,
                        ToolStoreUiState.Status.Installing -> Unit
                    }
                }
            )
        }
    }
}

private fun LazyStaggeredGridScope.toolsPanel(
    toolStorePagingItems: LazyPagingItems<ToolEntity>,
    hasInstalled: (ToolEntity) -> StateFlow<Boolean>,
    onAction: (username: String, toolId: String) -> Unit,
    onClick: (username: String, toolId: String) -> Unit
) {
    items(
        items = toolStorePagingItems.itemSnapshotList,
        key = { it!!.id },
    ) {
        val installed by hasInstalled(it!!).collectAsState()
        ToolCard(
            tool = it!!,
            actionIcon = if (installed) null else OxygenIcons.Download,
            actionIconContentDescription = stringResource(R.string.core_install),
            onAction = { onAction(it.authorUsername, it.toolId) },
            onClick = { onClick(it.authorUsername, it.toolId) },
            onLongClick = { onClick(it.authorUsername, it.toolId) },
        )
    }
}