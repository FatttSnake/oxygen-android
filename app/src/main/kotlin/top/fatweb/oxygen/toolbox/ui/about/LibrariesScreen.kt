package top.fatweb.oxygen.toolbox.ui.about

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.oxygen.toolbox.ui.component.scrollbar.scrollbarState
import top.fatweb.oxygen.toolbox.ui.theme.OxygenPreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme

@Composable
internal fun LibrariesRoute(
    modifier: Modifier = Modifier,
    viewModel: LibrariesScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val librariesScreenUiState by viewModel.librariesScreenUiState.collectAsStateWithLifecycle()

    LibrariesScreen(
        modifier = modifier.safeDrawingPadding(),
        librariesScreenUiState = librariesScreenUiState,
        onBackClick = onBackClick
    )
}

@Composable
internal fun LibrariesScreen(
    modifier: Modifier = Modifier,
    librariesScreenUiState: LibrariesScreenUiState,
    onBackClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val isLibrariesLoading = librariesScreenUiState is LibrariesScreenUiState.Loading

    ReportDrawnWhen { !isLibrariesLoading }

    val itemsAvailable = howManyItems(librariesScreenUiState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable = itemsAvailable)

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }
    var dialogUrl by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {
        LibrariesToolBar(
            onBackClick = onBackClick
        )
        Box(
            modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (librariesScreenUiState) {
                LibrariesScreenUiState.Loading -> {
                    Text(text = stringResource(R.string.feature_settings_loading))
                }

                is LibrariesScreenUiState.Success -> {
                    val handleOnClickLicense = { key: String ->
                        val license = librariesScreenUiState.dependencies.licenses[key]
                        if (license != null) {
                            showDialog = true
                            dialogTitle = license.name
                            dialogContent = license.content ?: ""
                            dialogUrl = license.url ?: ""
                        }
                    }

                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(300.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalItemSpacing = 24.dp,
                        state = state
                    ) {
                        librariesPanel(
                            librariesScreenUiState = librariesScreenUiState,
                            onClickLicense = handleOnClickLicense
                        )

                        item(span = StaggeredGridItemSpan.FullLine) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                        }
                    }

                    state.DraggableScrollbar(
                        modifier = Modifier
                            .fillMaxHeight()
                            .windowInsetsPadding(WindowInsets.systemBars)
                            .padding(horizontal = 2.dp)
                            .align(Alignment.CenterEnd),
                        state = scrollbarState, orientation = Orientation.Vertical,
                        onThumbMoved = state.rememberDraggableScroller(itemsAvailable = itemsAvailable)
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            modifier = Modifier
                .widthIn(max = configuration.screenWidthDp.dp - 80.dp)
                .heightIn(max = configuration.screenHeightDp.dp - 40.dp),
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        modifier = Modifier,
                        text = dialogContent
                    )
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(dialogUrl)))
                    }) {
                        Text(text = stringResource(R.string.core_website))
                    }
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = stringResource(R.string.core_close))
                    }
                }
            }
        )
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

fun howManyItems(librariesScreenUiState: LibrariesScreenUiState) =
    when (librariesScreenUiState) {
        LibrariesScreenUiState.Loading -> 0

        is LibrariesScreenUiState.Success -> librariesScreenUiState.dependencies.libraries.size
    }

@OxygenPreviews
@Composable
private fun LibrariesScreenLoadingPreview() {
    OxygenTheme {
        LibrariesScreen(librariesScreenUiState = LibrariesScreenUiState.Loading, onBackClick = {})
    }
}
