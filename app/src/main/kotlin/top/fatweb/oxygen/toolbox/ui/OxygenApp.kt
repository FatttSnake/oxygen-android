package top.fatweb.oxygen.toolbox.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.navigation.OxygenNavHost
import top.fatweb.oxygen.toolbox.navigation.STAR_ROUTE
import top.fatweb.oxygen.toolbox.navigation.TOOLS_ROUTE
import top.fatweb.oxygen.toolbox.navigation.TopLevelDestination
import top.fatweb.oxygen.toolbox.ui.component.OxygenBackground
import top.fatweb.oxygen.toolbox.ui.component.OxygenGradientBackground
import top.fatweb.oxygen.toolbox.ui.component.OxygenNavigationBar
import top.fatweb.oxygen.toolbox.ui.component.OxygenNavigationBarItem
import top.fatweb.oxygen.toolbox.ui.component.OxygenNavigationRail
import top.fatweb.oxygen.toolbox.ui.component.OxygenNavigationRailItem
import top.fatweb.oxygen.toolbox.ui.component.OxygenTopAppBar
import top.fatweb.oxygen.toolbox.ui.settings.SettingsDialog
import top.fatweb.oxygen.toolbox.ui.theme.GradientColors
import top.fatweb.oxygen.toolbox.ui.theme.LocalGradientColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OxygenApp(appState: OxygenAppState) {
    val shouldShowGradientBackground =
        appState.currentTopLevelDestination == TopLevelDestination.Tools
    var showSettingsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    OxygenBackground {
        OxygenGradientBackground(
            gradientColors = if (shouldShowGradientBackground) LocalGradientColors.current else GradientColors()
        ) {
            val destination = appState.currentTopLevelDestination

            val snackbarHostState = remember { SnackbarHostState() }

            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            val noConnectMessage = stringResource(R.string.core_no_connect)

            val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = noConnectMessage,
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    onDismiss = { showSettingsDialog = false },
                    onNavigateToLibraries = appState::navigateToLibraries,
                    onNavigateToAbout = appState::navigateToAbout
                )
            }

            Scaffold(
                modifier = Modifier
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    AnimatedVisibility(
                        visible = appState.shouldShowBottomBar && destination != null,
                        enter = slideInVertically { it }) {
                        BottomAppBar(
                            windowInsets = WindowInsets(0)
                        ) {
                            OxygenBottomBar(
                                destinations = appState.topLevelDestinations,
                                onNavigateToDestination = appState::navigateToTopLevelDestination,
                                currentDestination = appState.currentDestination
                            )
                        }
                    }
                }
            ) { padding ->
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal
                            )
                        )
                ) {
                    AnimatedVisibility(appState.shouldShowNavRail && destination != null) {
                        OxygenNavRail(
                            modifier = Modifier.safeDrawingPadding(),
                            destinations = appState.topLevelDestinations,
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination
                        )
                    }

                    Column(
                        Modifier.fillMaxSize()
                    ) {
                        if (destination != null) {
                            OxygenTopAppBar(
                                scrollBehavior = topAppBarScrollBehavior,
                                title = {
                                    Text(text = stringResource(id = destination.titleTextId))
                                },
                                navigationIcon = OxygenIcons.Search,
                                navigationIconContentDescription = stringResource(R.string.feature_settings_top_app_bar_navigation_icon_description),
                                actionIcon = OxygenIcons.MoreVert,
                                actionIconContentDescription = stringResource(R.string.feature_settings_top_app_bar_action_icon_description),
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Transparent,
                                    scrolledContainerColor = Color.Transparent
                                ),
                                onNavigationClick = { appState.navigateToSearch() },
                                onActionClick = { showSettingsDialog = true }
                            )
                        }

                        OxygenNavHost(
                            appState = appState,
                            onShowSnackbar = { message, action ->
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    actionLabel = action,
                                    duration = SnackbarDuration.Short
                                ) == SnackbarResult.ActionPerformed
                            },
                            startDestination = when (appState.launchPageConfig) {
                                LaunchPageConfig.Tools -> TOOLS_ROUTE
                                LaunchPageConfig.Star -> STAR_ROUTE
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OxygenBottomBar(
    modifier: Modifier = Modifier,
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?
) {
    OxygenNavigationBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            OxygenNavigationBarItem(
                modifier = modifier,
                selected = selected,
                label = { Text(stringResource(destination.titleTextId)) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = stringResource(destination.iconTextId)
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(destination.iconTextId)
                    )
                },
                onClick = { onNavigateToDestination(destination) }
            )
        }
    }
}

@Composable
private fun OxygenNavRail(
    modifier: Modifier = Modifier,
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?
) {
    OxygenNavigationRail(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            OxygenNavigationRailItem(
                modifier = modifier,
                selected = selected,
                label = { Text(stringResource(destination.titleTextId)) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = stringResource(destination.iconTextId)
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(destination.iconTextId)
                    )
                },
                onClick = { onNavigateToDestination(destination) }
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.equals(destination.route) ?: false
    } ?: false
