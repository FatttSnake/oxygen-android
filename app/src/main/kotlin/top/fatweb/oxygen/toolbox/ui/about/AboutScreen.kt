package top.fatweb.oxygen.toolbox.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
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

@Composable
internal fun AboutScreen(
    modifier: Modifier = Modifier, onBackClick: () -> Unit, onNavigateToLibraries: () -> Unit
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AboutToolBar(
            onBackClick = onBackClick
        )
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

@Composable
private fun AboutToolBar(
    modifier: Modifier = Modifier, onBackClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = OxygenIcons.ArrowBack,
                contentDescription = stringResource(R.string.core_back)
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
fun AboutToolBarPreview() {
    OxygenTheme {
        AboutToolBar(onBackClick = {})
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
