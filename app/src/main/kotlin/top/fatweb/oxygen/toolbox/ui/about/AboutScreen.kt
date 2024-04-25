package top.fatweb.oxygen.toolbox.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.ui.component.OxygenTopAppBar
import top.fatweb.oxygen.toolbox.ui.theme.OxygenPreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme
import top.fatweb.oxygen.toolbox.ui.util.ResourcesUtils

@Composable
internal fun AboutRoute(
    modifier: Modifier = Modifier, onBackClick: () -> Unit, onNavigateToLibraries: () -> Unit
) {
    AboutScreen(
        modifier = modifier.safeDrawingPadding(),
        onBackClick = onBackClick,
        onNavigateToLibraries = onNavigateToLibraries
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutScreen(
    modifier: Modifier = Modifier, onBackClick: () -> Unit, onNavigateToLibraries: () -> Unit
) {
    val scrollState = rememberScrollState()
    val topAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(canScroll = { scrollState.maxValue > 0 })

    Scaffold(
        modifier = Modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            OxygenTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                titleRes = R.string.feature_settings_more_about,
                navigationIcon = OxygenIcons.Back,
                navigationIconContentDescription = stringResource(R.string.core_back),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                onNavigationClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
                .verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier.height(64.dp)
            )
            AboutAppInfo()
            Spacer(
                modifier = Modifier.weight(1f)
            )
            AboutFooter(
                onNavigateToLibraries = onNavigateToLibraries
            )
        }
    }
}

@Composable
private fun AboutAppInfo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_oxygen),
            contentDescription = stringResource(R.string.app_full_name)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            text = stringResource(R.string.app_name)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline,
            text = stringResource(R.string.app_description)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            text = "${ResourcesUtils.getAppVersionName(LocalContext.current)}(${
                ResourcesUtils.getAppVersionCode(
                    LocalContext.current
                )
            })"
        )
    }
}

@Composable
private fun AboutFooter(
    modifier: Modifier = Modifier, onNavigateToLibraries: () -> Unit
) {
    Row(
        modifier = modifier.padding(32.dp)
    ) {
        TextButton(
            onClick = onNavigateToLibraries
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = stringResource(R.string.feature_settings_open_source_license)
            )
        }
    }
}

@OxygenPreviews
@Composable
fun AboutAppInfoPreview() {
    OxygenTheme {
        AboutAppInfo()
    }
}

@OxygenPreviews
@Composable
fun AboutScreenPreview() {
    OxygenTheme {
        AboutScreen(onBackClick = {}, onNavigateToLibraries = {})
    }
}
