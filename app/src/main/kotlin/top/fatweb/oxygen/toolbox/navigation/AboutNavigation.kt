package top.fatweb.oxygen.toolbox.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.oxygen.toolbox.ui.about.AboutRoute

const val ABOUT_ROUTE = "about_route"

fun NavController.navigateToAbout(navOptions: NavOptions? = null) =
    navigate(route = ABOUT_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.aboutScreen(
    onNavigateToLibraries: () -> Unit,
    onBackClick: () -> Unit
) {
    composable(
        route = ABOUT_ROUTE,
        enterTransition = {
            slideInHorizontally { it }
        },
        popEnterTransition = null,
        popExitTransition = {
            slideOutHorizontally { it }
        }
    ) {
        AboutRoute(
            onNavigateToLibraries = onNavigateToLibraries,
            onBackClick = onBackClick
        )
    }
}
