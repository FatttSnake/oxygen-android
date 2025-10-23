package top.fatweb.oxygen.toolbox.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.oxygen.toolbox.ui.star.StarRoute

const val STAR_ROUTE = "star_route"

fun NavController.navigateToStar(navOptions: NavOptions) =
    navigate(route = STAR_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.starScreen(
    isVertical: Boolean,
    searchValue: String,
    onNavigateToToolView: (username: String, toolId: String, preview: Boolean) -> Unit
) {
    composable(
        route = STAR_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                TOOL_STORE_ROUTE, TOOLS_ROUTE ->
                    if (isVertical) slideInHorizontally { it }
                    else slideInVertically { it }

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                TOOL_STORE_ROUTE, TOOLS_ROUTE ->
                    if (isVertical) slideOutHorizontally { it }
                    else slideOutVertically { it }

                else -> null
            }
        }
    ) {
        StarRoute(
            searchValue = searchValue,
            onNavigateToToolView = onNavigateToToolView
        )
    }
}
