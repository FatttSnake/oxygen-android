package top.fatweb.oxygen.toolbox.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.monitor.NetworkMonitor
import top.fatweb.oxygen.toolbox.monitor.TimeZoneMonitor
import top.fatweb.oxygen.toolbox.navigation.STAR_ROUTE
import top.fatweb.oxygen.toolbox.navigation.TOOLS_ROUTE
import top.fatweb.oxygen.toolbox.navigation.TOOL_STORE_ROUTE
import top.fatweb.oxygen.toolbox.navigation.TopLevelDestination
import top.fatweb.oxygen.toolbox.navigation.navigateToAbout
import top.fatweb.oxygen.toolbox.navigation.navigateToLibraries
import top.fatweb.oxygen.toolbox.navigation.navigateToStar
import top.fatweb.oxygen.toolbox.navigation.navigateToToolStore
import top.fatweb.oxygen.toolbox.navigation.navigateToTools
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberOxygenAppState(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    launchPageConfig: LaunchPageConfig
): OxygenAppState = remember(
    windowSizeClass,
    networkMonitor,
    timeZoneMonitor,
    coroutineScope,
    navController,
    launchPageConfig
) {
    OxygenAppState(
        windowSizeClass,
        networkMonitor,
        timeZoneMonitor,
        coroutineScope,
        navController,
        launchPageConfig
    )
}

@Stable
class OxygenAppState(
    private val windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope,
    val navController: NavHostController,
    val launchPageConfig: LaunchPageConfig
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            TOOL_STORE_ROUTE -> TopLevelDestination.ToolStore
            TOOLS_ROUTE -> TopLevelDestination.Tools
            STAR_ROUTE -> TopLevelDestination.Star
            else -> null
        }

    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            initialValue = false,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
        )

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            scope = coroutineScope,
            initialValue = TimeZone.currentSystemDefault(),
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
        )

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.ToolStore -> navController.navigateToToolStore(topLevelNavOptions)
            TopLevelDestination.Tools -> navController.navigateToTools(topLevelNavOptions)
            TopLevelDestination.Star -> navController.navigateToStar(topLevelNavOptions)
        }
    }

    fun navigateToLibraries() = navController.navigateToLibraries()

    fun navigateToAbout() = navController.navigateToAbout()
}