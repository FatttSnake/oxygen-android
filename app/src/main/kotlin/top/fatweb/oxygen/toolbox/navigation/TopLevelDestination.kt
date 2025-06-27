package top.fatweb.oxygen.toolbox.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons

enum class TopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int
) {
    ToolStore(
        route = TOOL_STORE_ROUTE,
        selectedIcon = OxygenIcons.Store,
        unselectedIcon = OxygenIcons.StoreBorder,
        iconTextId = R.string.feature_store_title,
        titleTextId = R.string.feature_store_title
    ),

    Tools(
        route = TOOLS_ROUTE,
        selectedIcon = OxygenIcons.Home,
        unselectedIcon = OxygenIcons.HomeBorder,
        iconTextId = R.string.feature_tools_title,
        titleTextId = R.string.feature_tools_title
    ),

    Star(
        route = STAR_ROUTE,
        selectedIcon = OxygenIcons.Star,
        unselectedIcon = OxygenIcons.StarBorder,
        iconTextId = R.string.feature_star_title,
        titleTextId = R.string.feature_star_title
    )
}

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.equals(destination.route) == true
    } == true
