package top.fatweb.oxygen.toolbox.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import top.fatweb.oxygen.toolbox.ui.OxygenAppState

@Composable
fun OxygenNavHost(
    modifier: Modifier = Modifier,
    appState: OxygenAppState,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean,
    startDestination: String,
    isVertical: Boolean,
    searchValue: String,
    searchCount: Int
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        aboutScreen(
            onBackClick = navController::popBackStack,
            onNavigateToLibraries = navController::navigateToLibraries
        )
        librariesScreen(
            onBackClick = navController::popBackStack
        )
        toolStoreScreen(
            isVertical = isVertical,
            searchValue = searchValue,
            searchCount = searchCount,
            onNavigateToToolView = navController::navigateToToolView
        )
        toolsScreen(
            isVertical = isVertical,
            searchValue = searchValue,
            onShowSnackbar = onShowSnackbar,
            onNavigateToToolView = navController::navigateToToolView,
            onNavigateToToolStore = { appState.navigateToTopLevelDestination(TopLevelDestination.ToolStore) }
        )
        toolViewScreen(
            onBackClick = navController::popBackStack
        )
        starScreen(
            isVertical = isVertical,
            searchValue = searchValue,
            onNavigateToToolView = navController::navigateToToolView
        )
    }
}