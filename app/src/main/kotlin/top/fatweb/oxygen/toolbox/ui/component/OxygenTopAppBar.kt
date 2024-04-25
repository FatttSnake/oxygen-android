package top.fatweb.oxygen.toolbox.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.ui.theme.OxygenPreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme
import android.R as androidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OxygenTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    @StringRes titleRes: Int,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    val topInset by animateIntAsState(
        if (scrollBehavior != null && -scrollBehavior.state.heightOffset >= with(LocalDensity.current) { 64.0.dp.toPx() }) 0
        else TopAppBarDefaults.windowInsets.getTop(LocalDensity.current), label = ""
    )
    CenterAlignedTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = { Text(stringResource(titleRes)) },
        navigationIcon = {
            navigationIcon?.let {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            actionIcon?.let {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = colors,
        windowInsets = WindowInsets(top = topInset)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@OxygenPreviews
@Composable
private fun OxygenTopAppBarPreview() {
    OxygenTheme {
        OxygenTopAppBar(
            titleRes = androidR.string.untitled,
            navigationIcon = OxygenIcons.Search,
            navigationIconContentDescription = "Navigation icon",
            actionIcon = OxygenIcons.MoreVert,
            actionIconContentDescription = "Action icon"
        )
    }
}