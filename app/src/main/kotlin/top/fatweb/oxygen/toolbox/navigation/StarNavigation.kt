package top.fatweb.oxygen.toolbox.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val STAR_ROUTE = "star_route"

fun NavController.navigateToStar(navOptions: NavOptions) = navigate(STAR_ROUTE, navOptions)

fun NavGraphBuilder.starScreen() {
    composable(
        route = STAR_ROUTE
    ) {

    }
}