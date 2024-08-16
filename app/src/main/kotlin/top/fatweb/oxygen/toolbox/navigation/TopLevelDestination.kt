package top.fatweb.oxygen.toolbox.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
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
        route = "tool_store_route",
        selectedIcon = OxygenIcons.Store,
        unselectedIcon = OxygenIcons.StoreBorder,
        iconTextId = R.string.feature_store_title,
        titleTextId = R.string.feature_store_title
    ),

    Tools(
        route = "tools_route",
        selectedIcon = OxygenIcons.Home,
        unselectedIcon = OxygenIcons.HomeBorder,
        iconTextId = R.string.feature_tools_title,
        titleTextId = R.string.feature_tools_title
    ),

    Star(
        route = "star_route",
        selectedIcon = OxygenIcons.Star,
        unselectedIcon = OxygenIcons.StarBorder,
        iconTextId = R.string.feature_star_title,
        titleTextId = R.string.feature_star_title
    )
}