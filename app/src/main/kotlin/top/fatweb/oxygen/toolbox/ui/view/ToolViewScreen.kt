package top.fatweb.oxygen.toolbox.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import top.fatweb.oxygen.toolbox.ui.util.LocalFullScreen
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
    val toolViewUiState by viewModel.toolViewUiState.collectAsStateWithLifecycle()
    val webViewInstanceState by viewModel.webviewInstance.collectAsStateWithLifecycle()
    val isPreview by viewModel.isPreview.collectAsStateWithLifecycle()

    ToolViewScreen(
        modifier = modifier,
        toolViewUiState = toolViewUiState,
        webViewInstanceState = webViewInstanceState,
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
    webViewInstanceState: WebViewInstanceState,
    isPreview: Boolean,
    onBackClick: () -> Unit
) {
    val (isFullScreen, onFullScreenStateChange) = LocalFullScreen.current

    Column(
        modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal
                )
            )
    ) {
        TopBar(
            toolViewUiState = toolViewUiState,
            isPreview = isPreview,
            isFullScreen = isFullScreen,
            onBackClick = onBackClick,
            onFullScreenChange = onFullScreenStateChange
        )
        Content(
            toolViewUiState = toolViewUiState,
            webViewInstanceState = webViewInstanceState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    toolViewUiState: ToolViewUiState,
    isPreview: Boolean,
    isFullScreen: Boolean,
    onBackClick: () -> Unit,
    onFullScreenChange: (Boolean) -> Unit
) = OxygenTopAppBar(
    title = {
        Text(
            text = when (toolViewUiState) {
                ToolViewUiState.Loading -> stringResource(R.string.core_loading)
                ToolViewUiState.Error -> stringResource(R.string.feature_tools_can_not_open)
                is ToolViewUiState.Success -> if (isPreview) stringResource(
                    R.string.feature_tool_view_preview_suffix,
                    toolViewUiState.toolName
                ) else toolViewUiState.toolName
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    navigationIcon = OxygenIcons.Back,
    navigationIconContentDescription = stringResource(R.string.core_back),
    actionIcon = if (isFullScreen) OxygenIcons.FullScreenExit else OxygenIcons.FullScreen,
    actionIconContentDescription = stringResource(if (isFullScreen) R.string.core_exit_full_screen else R.string.core_full_screen),
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent
    ),
    onNavigationClick = onBackClick,
    onActionClick = {
        onFullScreenChange(!isFullScreen)
    }
)

@Composable
private fun Content(
    toolViewUiState: ToolViewUiState,
    webViewInstanceState: WebViewInstanceState
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    var fileChooserCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    val fileChooserLauncher = rememberFileChooserLauncher(fileChooserCallback)
    val permissionLauncher = rememberPermissionLauncher()

    var isShowDialog = remember { mutableStateOf(false) }
    var dialogType = remember { mutableStateOf(DialogType.Alert) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogText = remember { mutableStateOf("") }
    var dialogInputValue = remember { mutableStateOf("") }
    var onDialogConfirm = remember { mutableStateOf<((String) -> Unit)?>(null) }
    var onDialogCancel = remember { mutableStateOf<(() -> Unit)?>(null) }

    when (webViewInstanceState) {
        WebViewInstanceState.Loading -> {
            Indicator()
        }

        is WebViewInstanceState.Success -> {
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
                    dialogTitle = toolViewUiState.toolName
                    val webViewState = rememberWebViewStateWithHTMLData(
                        data = toolViewUiState.htmlData,
                    )
                    WebView(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
                        state = webViewState,
                        chromeClient = rememberChromeClient(
                            context = context,
                            fileChooserLauncher = fileChooserLauncher,
                            isShowDialog = isShowDialog,
                            dialogType = dialogType,
                            dialogText = dialogText,
                            dialogInputValue = dialogInputValue,
                            onDialogConfirm = onDialogConfirm,
                            onDialogCancel = onDialogCancel
                        ) {
                            fileChooserCallback = it
                        },
                        onCreated = initWebView(
                            context = context,
                            permissionLauncher = permissionLauncher
                        ),
                        factory = {
                            webViewInstanceState.webView
                        }
                    )
                }
            }
        }
    }

    if (isShowDialog.value) {
        AlertDialog(
            modifier = Modifier
                .widthIn(max = configuration.screenWidthDp.dp - 80.dp)
                .heightIn(max = configuration.screenHeightDp.dp - 40.dp),
            onDismissRequest = {},
            title = {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Column(
                        modifier = Modifier.verticalScroll(state = rememberScrollState())
                    ) {
                        Text(text = dialogText.value)
                    }
                    if (dialogType.value == DialogType.Prompt) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = dialogInputValue.value,
                            onValueChange = {
                                dialogInputValue.value = it
                            },
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = {
                        isShowDialog.value = false
                        onDialogConfirm.value?.invoke(dialogInputValue.value)
                    }) {
                        Text(text = stringResource(R.string.core_ok))
                    }
                    if (dialogType.value != DialogType.Alert) {
                        TextButton(onClick = {
                            isShowDialog.value = false
                            onDialogCancel.value?.invoke()
                        }) {
                            Text(text = stringResource(R.string.core_cancel))
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun rememberFileChooserLauncher(fileChooserCallback: ValueCallback<Array<Uri>>?) =
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

@Composable
private fun rememberPermissionLauncher() =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        Permissions.continuation?.resume(it)
    }

@SuppressLint("SetJavaScriptEnabled")
private fun initWebView(
    context: Context,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) = { webview: WebView ->
    webview.settings.javaScriptEnabled = true
    webview.settings.domStorageEnabled = true
    webview.addJavascriptInterface(
        NativeWebApi(context = context, permissionLauncher = permissionLauncher),
        "NativeApi"
    )
    webview.setDownloadListener { url, userAgent, _, mimetype, _ ->
        if (!listOf("http://", "https://").any(url::startsWith)) {
            webview.evaluateJavascript(
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

@Composable
private fun rememberChromeClient(
    context: Context,
    fileChooserLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    isShowDialog: MutableState<Boolean>,
    dialogType: MutableState<DialogType>,
    dialogText: MutableState<String>,
    dialogInputValue: MutableState<String>,
    onDialogConfirm: MutableState<((String) -> Unit)?>,
    onDialogCancel: MutableState<(() -> Unit)?>,
    processCallback: (ValueCallback<Array<Uri>>?) -> Unit
) = remember {
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
            processCallback(filePathCallback)
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

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            isShowDialog.value = true
            dialogType.value = DialogType.Alert
            dialogText.value = message ?: ""
            onDialogConfirm.value = {
                result?.confirm()
            }

            return true
        }

        override fun onJsConfirm(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            isShowDialog.value = true
            dialogType.value = DialogType.Confirm
            dialogText.value = message ?: ""
            onDialogConfirm.value = {
                result?.confirm()
            }
            onDialogCancel.value = {
                result?.cancel()
            }

            return true
        }

        override fun onJsPrompt(
            view: WebView?,
            url: String?,
            message: String?,
            defaultValue: String?,
            result: JsPromptResult?
        ): Boolean {
            isShowDialog.value = true
            dialogType.value = DialogType.Prompt
            dialogText.value = message ?: ""
            dialogInputValue.value = defaultValue ?: ""
            onDialogConfirm.value = {
                result?.confirm(dialogInputValue.value)
            }
            onDialogCancel.value = {
                result?.cancel()
            }

            return true
        }
    }
}

private enum class DialogType {
    Alert, Confirm, Prompt
}
