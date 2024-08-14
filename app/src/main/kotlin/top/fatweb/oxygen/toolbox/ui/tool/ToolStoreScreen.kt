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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
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
        onChangeInstallStatus = viewModel::changeInstallInfo,
        onChangeInstallType = {
            viewModel.changeInstallInfo(type = it)
        },
        onInstallTool = viewModel::installTool,
        installInfo = installInfo
    )
}

@Composable
internal fun ToolStoreScreen(
    modifier: Modifier = Modifier,
    onNavigateToToolView: (username: String, toolId: String) -> Unit,
    toolStorePagingItems: LazyPagingItems<ToolEntity>,
    onChangeInstallStatus: (status: ToolStoreUiState.InstallInfo.Status) -> Unit,
    onChangeInstallType: (type: ToolStoreUiState.InstallInfo.Type) -> Unit,
    onInstallTool: (installTool: ToolEntity) -> Unit,
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

    var installTool by remember { mutableStateOf(ToolEntity("Unknown", "Unknown", "Unknown")) }

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
                onAction = { tool, installType ->
                    installTool = tool
                    onChangeInstallStatus(ToolStoreUiState.InstallInfo.Status.Pending)
                    onChangeInstallType(installType)
                },
                onClick = {
                    onNavigateToToolView(it.authorUsername, it.toolId)
                }
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

    InstallAlertDialog(
        installTool = installTool,
        installInfo = installInfo,
        onChangeInstallStatus = onChangeInstallStatus,
        onInstallTool = { onInstallTool(installTool) }
    )
}

private fun LazyStaggeredGridScope.toolsPanel(
    toolStorePagingItems: LazyPagingItems<ToolEntity>,
    onAction: (installTool: ToolEntity, installType: ToolStoreUiState.InstallInfo.Type) -> Unit,
    onClick: (ToolEntity) -> Unit
) {
    items(
        items = toolStorePagingItems.itemSnapshotList,
        key = { it!!.id },
    ) {
        ToolCard(
            tool = it!!,
            specifyVer = it.upgrade,
            actionIcon = if (it.upgrade != null) OxygenIcons.Upgrade else if (!it.isInstalled) OxygenIcons.Download else null,
            actionIconContentDescription = stringResource(R.string.core_install),
            onAction = {
                onAction(
                    it,
                    if (it.upgrade != null) ToolStoreUiState.InstallInfo.Type.Upgrade else ToolStoreUiState.InstallInfo.Type.Install
                )
            },
            onClick = { onClick(it) }
        )
    }
}

@Composable
private fun InstallAlertDialog(
    installTool: ToolEntity,
    installInfo: ToolStoreUiState.InstallInfo,
    onChangeInstallStatus: (ToolStoreUiState.InstallInfo.Status) -> Unit,
    onInstallTool: () -> Unit
) {
    val (status, type) = installInfo

    if (status != ToolStoreUiState.InstallInfo.Status.None) {
        AlertDialog(
            onDismissRequest = {
                if (status == ToolStoreUiState.InstallInfo.Status.Pending) {
                    onChangeInstallStatus(ToolStoreUiState.InstallInfo.Status.None)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (status) {
                            ToolStoreUiState.InstallInfo.Status.Success -> OxygenIcons.Success
                            ToolStoreUiState.InstallInfo.Status.Fail -> OxygenIcons.Error
                            else -> OxygenIcons.Info
                        },
                        contentDescription = stringResource(
                            when (type) {
                                ToolStoreUiState.InstallInfo.Type.Install -> R.string.core_install
                                ToolStoreUiState.InstallInfo.Type.Upgrade -> R.string.core_upgrade
                            }
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(
                            when (type) {
                                ToolStoreUiState.InstallInfo.Type.Install -> when (status) {
                                    ToolStoreUiState.InstallInfo.Status.Success -> R.string.feature_store_install_success
                                    ToolStoreUiState.InstallInfo.Status.Fail -> R.string.feature_store_install_fail
                                    else -> R.string.feature_store_install_tool
                                }

                                ToolStoreUiState.InstallInfo.Type.Upgrade -> when (status) {
                                    ToolStoreUiState.InstallInfo.Status.Success -> R.string.feature_store_upgrade_success
                                    ToolStoreUiState.InstallInfo.Status.Fail -> R.string.feature_store_upgrade_fail
                                    else -> R.string.feature_store_upgrade_tool
                                }
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
                    when (status) {
                        ToolStoreUiState.InstallInfo.Status.Pending ->
                            Text(
                                text = when (type) {
                                    ToolStoreUiState.InstallInfo.Type.Install -> stringResource(
                                        R.string.feature_store_ask_install,
                                        installTool.authorUsername,
                                        installTool.toolId
                                    )

                                    ToolStoreUiState.InstallInfo.Type.Upgrade -> stringResource(
                                        R.string.feature_store_ask_upgrade,
                                        installTool.authorUsername,
                                        installTool.toolId,
                                        installTool.ver,
                                        installTool.upgrade ?: installTool.ver
                                    )
                                }

                            )

                        ToolStoreUiState.InstallInfo.Status.Processing ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(
                                        when (type) {
                                            ToolStoreUiState.InstallInfo.Type.Install -> R.string.core_installing
                                            ToolStoreUiState.InstallInfo.Type.Upgrade -> R.string.core_upgrading
                                        }
                                    )
                                )
                            }

                        ToolStoreUiState.InstallInfo.Status.Success ->
                            Text(
                                text = stringResource(
                                    when (type) {
                                        ToolStoreUiState.InstallInfo.Type.Install -> R.string.feature_store_install_success_info
                                        ToolStoreUiState.InstallInfo.Type.Upgrade -> R.string.feature_store_upgrade_success_info
                                    }
                                )
                            )

                        ToolStoreUiState.InstallInfo.Status.Fail ->
                            Text(
                                text = stringResource(
                                    when (type) {
                                        ToolStoreUiState.InstallInfo.Type.Install -> R.string.feature_store_install_fail_info
                                        ToolStoreUiState.InstallInfo.Type.Upgrade -> R.string.feature_store_upgrade_fail_info
                                    }
                                )
                            )

                        else -> Unit
                    }
                }
            },
            dismissButton = {
                if (status == ToolStoreUiState.InstallInfo.Status.Pending) {
                    TextButton(onClick = {
                        onChangeInstallStatus(ToolStoreUiState.InstallInfo.Status.None)
                    }) {
                        Text(text = stringResource(R.string.core_cancel))
                    }
                }
            },
            confirmButton = {
                when (status) {
                    ToolStoreUiState.InstallInfo.Status.Pending ->
                        TextButton(onClick = onInstallTool) {
                            Text(text = stringResource(
                                when (type) {
                                    ToolStoreUiState.InstallInfo.Type.Install -> R.string.core_install
                                    ToolStoreUiState.InstallInfo.Type.Upgrade -> R.string.core_upgrade
                                }))
                        }

                    ToolStoreUiState.InstallInfo.Status.Success,
                    ToolStoreUiState.InstallInfo.Status.Fail ->
                        TextButton(onClick = {
                            onChangeInstallStatus(ToolStoreUiState.InstallInfo.Status.None)
                        }) {
                            Text(
                                text = stringResource(
                                    if (status == ToolStoreUiState.InstallInfo.Status.Success) R.string.core_ok
                                    else R.string.core_close
                                )
                            )
                        }

                    else -> Unit
                }
            }
        )
    }
}