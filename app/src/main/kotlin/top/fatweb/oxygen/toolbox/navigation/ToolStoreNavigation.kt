package top.fatweb.oxygen.toolbox.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.oxygen.toolbox.ui.store.ToolStoreRoute

const val TOOL_STORE_ROUTE = "tool_store_route"

fun NavController.navigateToToolStore(navOptions: NavOptions? = null) =
    navigate(TOOL_STORE_ROUTE, navOptions)

fun NavGraphBuilder.toolStoreScreen(
    isVertical: Boolean,
    searchValue: String,
    searchCount: Int,
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit
) {
    composable(
        route = TOOL_STORE_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                TOOLS_ROUTE, STAR_ROUTE ->
                    if (isVertical) slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth })
                    else slideInVertically(initialOffsetY = { fullHeight -> -fullHeight })

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                TOOLS_ROUTE, STAR_ROUTE ->
                    if (isVertical) slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
                    else slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight })

                else -> null
            }
        }
    ) {
        ToolStoreRoute(
            searchValue = searchValue,
            searchCount = searchCount,
            onNavigateToToolView = onNavigateToToolView
        )
    }
}
