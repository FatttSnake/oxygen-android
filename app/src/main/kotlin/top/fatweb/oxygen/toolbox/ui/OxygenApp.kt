package top.fatweb.oxygen.toolbox.ui

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.navigation.ABOUT_ROUTE
import top.fatweb.oxygen.toolbox.navigation.NavHost
import top.fatweb.oxygen.toolbox.navigation.STAR_ROUTE
import top.fatweb.oxygen.toolbox.navigation.TOOLS_ROUTE
import top.fatweb.oxygen.toolbox.navigation.TopLevelDestination
import top.fatweb.oxygen.toolbox.navigation.isTopLevelDestinationInHierarchy
import top.fatweb.oxygen.toolbox.ui.component.Background
import top.fatweb.oxygen.toolbox.ui.component.GradientBackground
import top.fatweb.oxygen.toolbox.ui.component.NavigationBar
import top.fatweb.oxygen.toolbox.ui.component.NavigationBarItem
import top.fatweb.oxygen.toolbox.ui.component.NavigationRail
import top.fatweb.oxygen.toolbox.ui.component.NavigationRailItem
import top.fatweb.oxygen.toolbox.ui.component.SearchButtonPosition
import top.fatweb.oxygen.toolbox.ui.component.TopAppBar
import top.fatweb.oxygen.toolbox.ui.settings.SettingsDialog
import top.fatweb.oxygen.toolbox.ui.theme.GradientColors
import top.fatweb.oxygen.toolbox.ui.theme.LocalGradientColors
import top.fatweb.oxygen.toolbox.ui.util.FullScreen
import top.fatweb.oxygen.toolbox.ui.util.LocalFullScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OxygenApp(
    appState: OxygenAppState,
    showSettingsDialogState: MutableState<Boolean>
) {
    val shouldShowGradientBackground =
        appState.currentDestination?.route == ABOUT_ROUTE
    var showSettingsDialog by showSettingsDialogState

    val context = LocalContext.current
    val window = (context as ComponentActivity).window
    val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
    var isFullScreen by remember { mutableStateOf(false) }

    val fullScreen = FullScreen(
        enable = isFullScreen,
        onStateChange = {
            isFullScreen = it
        }
    )

    DisposableEffect(Unit) {
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val listener = WindowInsetsControllerCompat.OnControllableInsetsChangedListener { _, _ ->
            isFullScreen = false
        }
        windowInsetsController.addOnControllableInsetsChangedListener(listener)
        onDispose {
            windowInsetsController.removeOnControllableInsetsChangedListener(listener)
        }
    }

    LaunchedEffect(isFullScreen) {
        if (isFullScreen) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    CompositionLocalProvider(LocalFullScreen provides fullScreen) {
        Background {
            GradientBackground(
                gradientColors = if (shouldShowGradientBackground) LocalGradientColors.current else GradientColors()
            ) {
                val destination = appState.currentTopLevelDestination

                val snackbarHostState = remember { SnackbarHostState() }

                val isOffline by appState.isOffline.collectAsStateWithLifecycle()

                val noConnectMessage = stringResource(R.string.core_no_connect)

                var canScroll by remember { mutableStateOf(true) }
                val topAppBarScrollBehavior =
                    if (canScroll) TopAppBarDefaults.enterAlwaysScrollBehavior() else TopAppBarDefaults.pinnedScrollBehavior()

                var activeSearch by remember { mutableStateOf(false) }
                var searchValue by remember { mutableStateOf("") }
                var searchCount by remember { mutableIntStateOf(0) }

                LaunchedEffect(activeSearch) {
                    canScroll = !activeSearch
                }

                LaunchedEffect(destination) {
                    activeSearch = false
                    searchValue = ""
                    if (searchCount == 0) {
                        searchCount++
                    } else {
                        searchCount = 0
                    }
                }

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
                        .nestedScroll(connection = topAppBarScrollBehavior.nestedScrollConnection),
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    bottomBar = {
                        AnimatedVisibility(
                            visible = appState.shouldShowBottomBar && destination != null
                        ) {
                            BottomBar(
                                destinations = appState.topLevelDestinations,
                                currentDestination = appState.currentDestination,
                                onNavigateToDestination = appState::navigateToTopLevelDestination
                            )
                        }
                    }
                ) { padding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        AnimatedVisibility(
                            visible = appState.shouldShowNavRail && destination != null
                        ) {
                            NavRail(
                                modifier = Modifier
                                    .padding(padding)
                                    .consumeWindowInsets(padding)
                                    .safeDrawingPadding(),
                                destinations = appState.topLevelDestinations,
                                currentDestination = appState.currentDestination,
                                onNavigateToDestination = appState::navigateToTopLevelDestination
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            AnimatedVisibility(
                                visible = destination != null
                            ) {
                                TopAppBar(
                                    scrollBehavior = topAppBarScrollBehavior,
                                    title = {
                                        destination?.let {
                                            Text(
                                                text = stringResource(destination.titleTextId),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    },
                                    navigationIcon = OxygenIcons.Search,
                                    navigationIconContentDescription = stringResource(R.string.feature_settings_top_app_bar_navigation_icon_description),
                                    actionIcon = OxygenIcons.MoreVert,
                                    actionIconContentDescription = stringResource(R.string.feature_settings_top_app_bar_action_icon_description),
                                    activeSearch = activeSearch,
                                    searchButtonPosition = SearchButtonPosition.Navigation,
                                    query = searchValue,
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = Color.Transparent,
                                        scrolledContainerColor = Color.Transparent
                                    ),
                                    onNavigationClick = { activeSearch = true },
                                    onActionClick = { showSettingsDialog = true },
                                    onQueryChange = {
                                        searchValue = it
                                    },
                                    onSearch = {
                                        searchCount++
                                    },
                                    onCancelSearch = {
                                        searchValue = ""
                                        activeSearch = false
                                        searchCount = 0
                                    }
                                )
                            }

                            NavHost(
                                appState = appState,
                                startDestination = when (appState.launchPageConfig) {
                                    LaunchPageConfig.Tools -> TOOLS_ROUTE
                                    LaunchPageConfig.Star -> STAR_ROUTE
                                },
                                isVertical = appState.shouldShowBottomBar,
                                searchValue = searchValue,
                                searchCount = searchCount,
                                onShowSnackbar = { message, action ->
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = action,
                                        duration = SnackbarDuration.Short
                                    ) == SnackbarResult.ActionPerformed
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationBarItem(
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
private fun NavRail(
    modifier: Modifier = Modifier,
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    NavigationRail(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationRailItem(
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
