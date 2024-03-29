package top.fatweb.oxygen.toolbox.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import top.fatweb.oxygen.toolbox.navigation.TopLevelDestination
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme

@Composable
fun RowScope.OxygenNavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    label: @Composable (() -> Unit)? = null,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = false
) {
    NavigationBarItem(
        modifier = modifier,
        selected = selected,
        label = label,
        icon = if (selected) selectedIcon else icon,
        onClick = onClick,
        enabled = enabled,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = OxygenNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = OxygenNavigationDefaults.navigationContentColor(),
            selectedTextColor = OxygenNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = OxygenNavigationDefaults.navigationContentColor(),
            indicatorColor = OxygenNavigationDefaults.navigationIndicatorColor()
        )
    )
}

@Composable
fun OxygenNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        contentColor = OxygenNavigationDefaults.navigationContentColor(),
        content = content,
        tonalElevation = 0.dp
    )
}

@Composable
fun OxygenNavigationRailItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    label: @Composable (() -> Unit)? = null,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true
) {
    NavigationRailItem(
        modifier = modifier,
        selected = selected,
        label = label,
        icon = if (selected) selectedIcon else icon,
        onClick = onClick,
        enabled = enabled,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = OxygenNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = OxygenNavigationDefaults.navigationContentColor(),
            selectedTextColor = OxygenNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = OxygenNavigationDefaults.navigationContentColor(),
            indicatorColor = OxygenNavigationDefaults.navigationIndicatorColor()
        )
    )
}

@Composable
fun OxygenNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    NavigationRail(
        modifier = modifier,
        header = header,
        contentColor = OxygenNavigationDefaults.navigationContentColor(),
        content = content,
        containerColor = Color.Transparent
    )
}

object OxygenNavigationDefaults {
    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}

@ThemePreviews
@Composable
fun OxygenNavigationBarPreview() {
    val items = TopLevelDestination.entries

    OxygenTheme {
        OxygenNavigationBar {
            items.forEachIndexed { index, item ->
                OxygenNavigationBarItem(
                    selected = index == 0,
                    label = { Text(stringResource(item.titleTextId)) },
                    icon = {
                        Icon(
                            imageVector = item.unselectedIcon,
                            contentDescription = stringResource(item.iconTextId)
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = item.selectedIcon, contentDescription = stringResource(
                                item.iconTextId
                            )
                        )
                    },
                    onClick = {}
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun OxygenNavigationRailPreview() {
    val items = TopLevelDestination.entries

    OxygenTheme {
        OxygenNavigationRail {
            items.forEachIndexed { index, item ->
                OxygenNavigationRailItem(
                    selected = index == 0,
                    label = { Text(stringResource(item.titleTextId)) },
                    icon = {
                        Icon(
                            imageVector = item.unselectedIcon,
                            contentDescription = stringResource(item.iconTextId)
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = item.selectedIcon, contentDescription = stringResource(
                                item.iconTextId
                            )
                        )
                    },
                    onClick = {}
                )
            }
        }
    }
}