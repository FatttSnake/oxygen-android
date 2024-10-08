package top.fatweb.oxygen.toolbox.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import top.fatweb.oxygen.toolbox.ui.OxygenAppState

@Composable
fun OxygenNavHost(
    modifier: Modifier = Modifier,
    appState: OxygenAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    startDestination: String
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        searchScreen(
            onBackClick = navController::popBackStack
        )
        toolsScreen(

        )
        starScreen(

        )
    }
}