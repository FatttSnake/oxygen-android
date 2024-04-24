package top.fatweb.oxygen.toolbox.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.oxygen.toolbox.ui.about.LibrariesRoute

const val LIBRARIES_ROUTE = "libraries_route"

fun NavController.navigateToLibraries(navOptions: NavOptions? = null) =
    navigate(LIBRARIES_ROUTE, navOptions)

fun NavGraphBuilder.librariesScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = LIBRARIES_ROUTE,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth })
        },
        popEnterTransition = null,
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
        }
    ) {
        LibrariesRoute(onBackClick = onBackClick)
    }
}