package top.fatweb.oxygen.toolbox.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.oxygen.toolbox.ui.tool.ToolStoreRoute

const val TOOL_STORE_ROUTE = "tool_store_route"

fun NavController.navigateToToolStore(navOptions: NavOptions? = null) = navigate(TOOL_STORE_ROUTE, navOptions)

fun NavGraphBuilder.toolStoreScreen(
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit
) {
    composable(
        route = TOOL_STORE_ROUTE
    ) {
        ToolStoreRoute(
            onNavigateToToolView = onNavigateToToolView
        )
    }
}
