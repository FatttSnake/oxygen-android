package top.fatweb.oxygen.toolbox.ui.search

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.fatweb.oxygen.toolbox.icon.OxygenIcons

@Composable
internal fun SearchRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
//    searchViewmodel: SearchViewModel = hiltViewModel()
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = OxygenIcons.Back, contentDescription = null)
        }
        Text("Search")
    }
}