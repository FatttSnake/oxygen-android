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

fun NavController.navigateToTools(navOptions: NavOptions) = navigate(TOOLS_ROUTE, navOptions)

fun NavGraphBuilder.toolsScreen(
    isVertical: Boolean,
    searchValue: String,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean,
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit,
    onNavigateToToolStore: () -> Unit
) {
    composable(
        route = TOOLS_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                TOOL_STORE_ROUTE ->
                    if (isVertical) slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth })
                    else slideInVertically(initialOffsetY = { fullHeight -> fullHeight })

                STAR_ROUTE ->
                    if (isVertical) slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth })
                    else slideInVertically(initialOffsetY = { fullHeight -> -fullHeight })

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                TOOL_STORE_ROUTE ->
                    if (isVertical) slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                    else slideOutVertically(targetOffsetY = { fullHeight -> fullHeight })

                STAR_ROUTE ->
                    if (isVertical) slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
                    else slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight })

                else -> null
            }
        }
    ) {
        ToolsRoute(
            searchValue = searchValue,
            onShowSnackbar = onShowSnackbar,
            onNavigateToToolView = onNavigateToToolView,
            onNavigateToToolStore = onNavigateToToolStore
        )
    }
}