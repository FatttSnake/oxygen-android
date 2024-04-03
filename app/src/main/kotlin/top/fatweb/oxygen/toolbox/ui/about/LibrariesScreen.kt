package top.fatweb.oxygen.toolbox.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons

@Composable
internal fun LibrariesRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    LibrariesScreen(
        modifier = modifier,
        onBackClick = onBackClick
    )
}

@Composable
internal fun LibrariesScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LibrariesToolBar(
            onBackClick = onBackClick
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun LibrariesToolBar(
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