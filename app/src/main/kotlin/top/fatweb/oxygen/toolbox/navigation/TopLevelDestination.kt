package top.fatweb.oxygen.toolbox.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int
) {
    TOOL_STORE(
        selectedIcon = OxygenIcons.Store,
        unselectedIcon = OxygenIcons.StoreBorder,
        iconTextId = R.string.feature_store_title,
        titleTextId = R.string.feature_store_title
    ),

    TOOLS(
        selectedIcon = OxygenIcons.Home,
        unselectedIcon = OxygenIcons.HomeBorder,
        iconTextId = R.string.feature_tools_title,
        titleTextId = R.string.feature_tools_title
    ),

    STAR(
        selectedIcon = OxygenIcons.Star,
        unselectedIcon = OxygenIcons.StarBorder,
        iconTextId = R.string.feature_star_title,
        titleTextId = R.string.feature_star_title
    )
}