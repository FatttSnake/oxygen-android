package top.fatweb.oxygen.toolbox.ui.view

import android.annotation.SuppressLint
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewStateWithHTMLData
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.Loading
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.ui.component.OxygenTopAppBar

@Composable
internal fun ToolViewRoute(
    modifier: Modifier = Modifier,
    viewModel: ToolViewScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val isPreview by viewModel.isPreview.collectAsStateWithLifecycle()
    val toolViewUiState by viewModel.toolViewUiState.collectAsStateWithLifecycle()

    ToolViewScreen(
        modifier = modifier,
        toolViewUiState = toolViewUiState,
        isPreview = isPreview,
        onBackClick = onBackClick
    )
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ToolViewScreen(
    modifier: Modifier = Modifier,
    toolViewUiState: ToolViewUiState,
    isPreview: Boolean,
    onBackClick: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    Scaffold(
        modifier = Modifier,
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    ) { padding ->
        Column(
            modifier
                .fillMaxWidth()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
        ) {
            OxygenTopAppBar(
                modifier = Modifier.zIndex(100f),
                title = {
                    Text(
                        text = when (toolViewUiState) {
                            ToolViewUiState.Loading -> stringResource(R.string.core_loading)
                            ToolViewUiState.Error -> stringResource(R.string.feature_tools_can_not_open)
                            is ToolViewUiState.Success -> if (isPreview) stringResource(
                                R.string.feature_tool_view_preview_suffix,
                                toolViewUiState.toolName
                            ) else toolViewUiState.toolName
                        }
                    )
                },
                navigationIcon = OxygenIcons.Back,
                navigationIconContentDescription = stringResource(R.string.core_back),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                onNavigationClick = onBackClick
            )
            when (toolViewUiState) {
                ToolViewUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val angle by infiniteTransition.animateFloat(
                            initialValue = 0F,
                            targetValue = 360F,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 800, easing = Ease),
                            ), label = "angle"
                        )
                        Icon(
                            modifier = Modifier
                                .size(32.dp)
                                .graphicsLayer { rotationZ = angle },
                            imageVector = Loading,
                            contentDescription = ""
                        )
                    }
                }

                ToolViewUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = stringResource(R.string.feature_tools_can_not_open))
                    }
                }

                is ToolViewUiState.Success -> {
                    val webViewState = rememberWebViewStateWithHTMLData(
                        data = toolViewUiState.htmlData,
                    )
                    WebView(
                        modifier = Modifier.fillMaxSize(),
                        state = webViewState,
                        onCreated = {
                            it.settings.javaScriptEnabled = true
                        })
                }
            }
        }
    }
}
