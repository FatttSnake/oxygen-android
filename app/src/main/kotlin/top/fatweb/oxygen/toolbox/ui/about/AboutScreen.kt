package top.fatweb.oxygen.toolbox.ui.about

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.ui.component.OxygenTopAppBar
import top.fatweb.oxygen.toolbox.ui.theme.OxygenPreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme
import top.fatweb.oxygen.toolbox.ui.util.ResourcesUtils

@Composable
internal fun AboutRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNavigateToLibraries: () -> Unit
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
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNavigateToLibraries: () -> Unit
) {
    val scrollState = rememberScrollState()
    val topAppBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(canScroll = { scrollState.maxValue > 0 })

    Scaffold(
        modifier = Modifier
            .nestedScroll(connection = topAppBarScrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OxygenTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                title = {
                    Text(text = stringResource(R.string.feature_settings_more_about))
                },
                navigationIcon = OxygenIcons.Back,
                navigationIconContentDescription = stringResource(R.string.core_back),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                onNavigationClick = onBackClick
            )
            Spacer(Modifier.height(64.dp))
            AboutAppInfo()
            Spacer(Modifier.weight(1f))
            AboutFooter(
                onNavigateToLibraries = onNavigateToLibraries
            )
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun AboutAppInfo(
    modifier: Modifier = Modifier
) {
    val logo = AnimatedImageVector.animatedVectorResource(R.drawable.ic_launcher)
    var atEnd by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(AnimationConstants.DefaultDurationMillis.toLong())
        atEnd = true
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .width(160.dp),
            painter = rememberAnimatedVectorPainter(animatedImageVector = logo, atEnd = atEnd),
            contentDescription = stringResource(R.string.app_full_name)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            text = stringResource(R.string.app_name)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline,
            text = stringResource(R.string.app_description)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            text = "${ResourcesUtils.getAppVersionName(LocalContext.current)} (${
                stringResource(
                    if (ResourcesUtils.getAppVersionCode(LocalContext.current) % 100 == 0L)
                        R.string.core_ga_version
                    else
                        R.string.core_beta_version
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
private fun AboutAppInfoPreview() {
    OxygenTheme {
        AboutAppInfo()
    }
}

@OxygenPreviews
@Composable
private fun AboutScreenPreview() {
    OxygenTheme {
        AboutScreen(onBackClick = {}, onNavigateToLibraries = {})
    }
}
