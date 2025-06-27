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
import top.fatweb.oxygen.toolbox.ui.theme.OxygenPreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme

@Composable
fun RowScope.NavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    label: @Composable (() -> Unit)? = null,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = false,
    onClick: () -> Unit
) {
    NavigationBarItem(
        modifier = modifier,
        selected = selected,
        label = label,
        icon = if (selected) selectedIcon else icon,
        enabled = enabled,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = NavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NavigationDefaults.navigationContentColor(),
            selectedTextColor = NavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NavigationDefaults.navigationContentColor(),
            indicatorColor = NavigationDefaults.navigationIndicatorColor()
        ),
        onClick = onClick
    )
}

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        contentColor = NavigationDefaults.navigationContentColor(),
        content = content,
        tonalElevation = 0.dp
    )
}

@Composable
fun NavigationRailItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    label: @Composable (() -> Unit)? = null,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    onClick: () -> Unit
) {
    NavigationRailItem(
        modifier = modifier,
        selected = selected,
        label = label,
        icon = if (selected) selectedIcon else icon,
        enabled = enabled,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = NavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NavigationDefaults.navigationContentColor(),
            selectedTextColor = NavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NavigationDefaults.navigationContentColor(),
            indicatorColor = NavigationDefaults.navigationIndicatorColor()
        ),
        onClick = onClick
    )
}

@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    NavigationRail(
        modifier = modifier,
        header = header,
        contentColor = NavigationDefaults.navigationContentColor(),
        content = content,
        containerColor = Color.Transparent
    )
}

object NavigationDefaults {
    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}

@OxygenPreviews
@Composable
private fun NavigationBarPreview() {
    val items = TopLevelDestination.entries

    OxygenTheme {
        top.fatweb.oxygen.toolbox.ui.component.NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
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

@OxygenPreviews
@Composable
private fun NavigationRailPreview() {
    val items = TopLevelDestination.entries

    OxygenTheme {
        top.fatweb.oxygen.toolbox.ui.component.NavigationRail {
            items.forEachIndexed { index, item ->
                NavigationRailItem(
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
