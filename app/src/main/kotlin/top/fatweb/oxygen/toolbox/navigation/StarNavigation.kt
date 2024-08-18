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

fun NavController.navigateToStar(navOptions: NavOptions) = navigate(STAR_ROUTE, navOptions)

fun NavGraphBuilder.starScreen(
    isVertical: Boolean
) {
    composable(
        route = STAR_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                TOOL_STORE_ROUTE, TOOLS_ROUTE ->
                    if (isVertical) slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth })
                    else slideInVertically(initialOffsetY = { fullHeight -> fullHeight })

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                TOOL_STORE_ROUTE, TOOLS_ROUTE ->
                    if (isVertical) slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                    else slideOutVertically(targetOffsetY = { fullHeight -> fullHeight })

                else -> null
            }
        }
    ) {
        StarRoute()
    }
}