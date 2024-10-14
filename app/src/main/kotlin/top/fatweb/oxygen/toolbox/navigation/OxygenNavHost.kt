package top.fatweb.oxygen.toolbox.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import top.fatweb.oxygen.toolbox.ui.OxygenAppState
import top.fatweb.oxygen.toolbox.ui.util.LocalFullScreen

@Composable
fun OxygenNavHost(
    modifier: Modifier = Modifier,
    appState: OxygenAppState,
    startDestination: String,
    isVertical: Boolean,
    searchValue: String,
    searchCount: Int,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    val navController = appState.navController
    val fullScreen = LocalFullScreen.current

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                fullScreen.onStateChange.invoke(false)
            }
        })
    }

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
        starScreen(
            isVertical = isVertical,
            searchValue = searchValue,
            onNavigateToToolView = navController::navigateToToolView
        )
        toolViewScreen(
            onBackClick = navController::popBackStack
        )
    }
}
