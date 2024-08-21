package top.fatweb.oxygen.toolbox.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.oxygen.toolbox.ui.tools.ToolsRoute

const val TOOLS_ROUTE = "tools_route"

fun NavController.navigateToTools(navOptions: NavOptions) =
    navigate(route = TOOLS_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.toolsScreen(
    isVertical: Boolean,
    searchValue: String,
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit,
    onNavigateToToolStore: () -> Unit,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    composable(
        route = TOOLS_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                TOOL_STORE_ROUTE ->
                    if (isVertical) slideInHorizontally { it }
                    else slideInVertically { it }

                STAR_ROUTE ->
                    if (isVertical) slideInHorizontally { -it }
                    else slideInVertically { -it }

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                TOOL_STORE_ROUTE ->
                    if (isVertical) slideOutHorizontally { it }
                    else slideOutVertically { it }

                STAR_ROUTE ->
                    if (isVertical) slideOutHorizontally { -it }
                    else slideOutVertically { -it }

                else -> null
            }
        }
    ) {
        ToolsRoute(
            searchValue = searchValue,
            onNavigateToToolView = onNavigateToToolView,
            onNavigateToToolStore = onNavigateToToolStore,
            onShowSnackbar = onShowSnackbar
        )
    }
}
