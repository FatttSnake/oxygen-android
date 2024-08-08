package top.fatweb.oxygen.toolbox.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.oxygen.toolbox.ui.tool.ToolsRoute

const val TOOLS_ROUTE = "tools_route"

fun NavController.navigateToTools(navOptions: NavOptions) = navigate(TOOLS_ROUTE, navOptions)

fun NavGraphBuilder.toolsScreen(
    onNavigateToToolView: (username: String, toolId: String) -> Unit,
    onNavigateToToolStore: () -> Unit
) {
    composable(
        route = TOOLS_ROUTE
    ) {
        ToolsRoute(
            onNavigateToToolView = onNavigateToToolView,
            onNavigateToToolStore = onNavigateToToolStore
        )
    }
}