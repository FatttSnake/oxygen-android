package top.fatweb.oxygen.toolbox.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val TOOLS_ROUTE = "tools_route"

fun NavController.navigateToTools(navOptions: NavOptions) = navigate(TOOLS_ROUTE, navOptions)

fun NavGraphBuilder.toolsScreen() {
    composable(
        route = TOOLS_ROUTE
    ) { }
}