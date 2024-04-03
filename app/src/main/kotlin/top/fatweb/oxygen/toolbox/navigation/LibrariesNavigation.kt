package top.fatweb.oxygen.toolbox.navigation

import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
            scaleIn()
        },
        exitTransition = {
            scaleOut()
        },
        popEnterTransition = {
            scaleIn()
        },
        popExitTransition = {
            scaleOut()
        }
    ) {
        LibrariesRoute(onBackClick = onBackClick)
    }
}