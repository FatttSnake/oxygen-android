package top.fatweb.oxygen.toolbox.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinnzou.web.AccompanistWebChromeClient
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewStateWithHTMLData
import timber.log.Timber
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.ui.component.Indicator
import top.fatweb.oxygen.toolbox.ui.component.OxygenTopAppBar
import top.fatweb.oxygen.toolbox.ui.util.ResourcesUtils
import top.fatweb.oxygen.toolbox.util.NativeWebApi
import top.fatweb.oxygen.toolbox.util.Permissions
import kotlin.coroutines.resume

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
    val context = LocalContext.current

    var fileChooserCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }

    val fileChooserLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    fileChooserCallback?.onReceiveValue(arrayOf(uri))
                } ?: {
                    fileChooserCallback?.onReceiveValue(emptyArray())
                }
            } else {
                fileChooserCallback?.onReceiveValue(emptyArray())
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            Permissions.continuation?.resume(it)
        }

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
                    Indicator()
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
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
                        state = webViewState,
                        chromeClient = remember {
                            object : AccompanistWebChromeClient() {
                                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                    consoleMessage?.let {
                                        Timber.tag("WebView").log(
                                            priority = when (it.messageLevel()) {
                                                ConsoleMessage.MessageLevel.TIP -> Log.VERBOSE
                                                ConsoleMessage.MessageLevel.LOG -> Log.INFO
                                                ConsoleMessage.MessageLevel.WARNING -> Log.WARN
                                                ConsoleMessage.MessageLevel.ERROR -> Log.ERROR
                                                else -> Log.DEBUG
                                            },
                                            message = "${it.message()} (${it.lineNumber()})"
                                        )
                                        return true
                                    }
                                    return false
                                }

                                override fun onShowFileChooser(
                                    webView: WebView?,
                                    filePathCallback: ValueCallback<Array<Uri>>?,
                                    fileChooserParams: FileChooserParams?
                                ): Boolean {
                                    fileChooserCallback = filePathCallback
                                    val intent = fileChooserParams?.createIntent()
                                        ?: Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                            addCategory(Intent.CATEGORY_OPENABLE)
                                            type = "*/*"
                                        }
                                    fileChooserLauncher.launch(
                                        Intent.createChooser(
                                            intent,
                                            fileChooserParams?.title ?: ResourcesUtils.getString(
                                                context = context,
                                                resId = R.string.core_file_select_one_text
                                            )
                                        )
                                    )

                                    return true
                                }
                            }
                        },
                        onCreated = {
                            it.settings.javaScriptEnabled = true
                            it.settings.domStorageEnabled = true
                            it.addJavascriptInterface(
                                NativeWebApi(context = context, webView = it, permissionLauncher),
                                "NativeApi"
                            )
                            it.setDownloadListener { url, userAgent, _, mimetype, _ ->
                                if (!listOf("http://", "https://").any(url::startsWith)) {
                                    it.evaluateJavascript(
                                        "alert('${
                                            ResourcesUtils.getString(
                                                context = context,
                                                resId = R.string.core_can_only_download_http_https,
                                                url
                                            )
                                        }')"
                                    ) {}
                                    return@setDownloadListener
                                }
                                val request = DownloadManager.Request(Uri.parse(url)).apply {
                                    addRequestHeader("User-Agent", userAgent)
                                    setMimeType(mimetype)
                                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                }
                                val downloadManager: DownloadManager =
                                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                downloadManager.enqueue(request)
                            }
                        }
                    )
                }
            }
        }
    }
}
