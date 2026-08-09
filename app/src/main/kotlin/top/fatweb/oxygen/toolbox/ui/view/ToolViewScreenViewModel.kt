package top.fatweb.oxygen.toolbox.ui.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.webkit.WebView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity
import top.fatweb.oxygen.toolbox.model.userdata.ThemeModeConfig
import top.fatweb.oxygen.toolbox.navigation.ToolViewArgs
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolStoreRepository
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository
import top.fatweb.oxygen.toolbox.ui.util.ResourcesHelper
import javax.inject.Inject
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ToolViewScreenViewModel @Inject constructor(
    @ApplicationContext context: Context,
    userDataRepository: UserDataRepository,
    toolRepository: ToolRepository,
    toolStoreRepository: ToolStoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isPreview = savedStateHandle.getStateFlow(IS_PREVIEW, false)

    private val toolViewArgs = ToolViewArgs(savedStateHandle)
    private val username = toolViewArgs.username
    private val toolId = toolViewArgs.toolId
    private val preview = toolViewArgs.preview

    private val toolViewDataCache = MutableStateFlow<ToolViewDataCache?>(null)

    val toolViewUiState: StateFlow<ToolViewUiState> = createToolViewUiState(
        context = context,
        savedStateHandle = savedStateHandle,
        username = username,
        toolId = toolId,
        preview = preview,
        userDataRepository = userDataRepository,
        toolRepository = toolRepository,
        toolStoreRepository = toolStoreRepository,
        toolViewDataCache = toolViewDataCache
    )
        .stateIn(
            scope = viewModelScope,
            initialValue = ToolViewUiState.Loading,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds)
        )

    val webviewInstance = createWebViewInstanceState(context)
        .stateIn(
            scope = viewModelScope,
            initialValue = WebViewInstanceState.Loading,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds)
        )
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun createToolViewUiState(
    context: Context,
    savedStateHandle: SavedStateHandle,
    username: String,
    toolId: String,
    preview: Boolean,
    userDataRepository: UserDataRepository,
    toolRepository: ToolRepository,
    toolStoreRepository: ToolStoreRepository,
    toolViewDataCache: MutableStateFlow<ToolViewDataCache?>,
): Flow<ToolViewUiState> {
    val isSystemDarkModeFlow = createSystemDarkModeFlow(context)
    val toolWithDistFlow = if (!preview) {
        toolRepository.getToolByUsernameAndToolId(username, toolId)
    } else {
        flowOf(null)
    }
    val toolBaseWithDistFlow = toolWithDistFlow.flatMapLatest { toolWithDist ->
        if (toolWithDist != null) {
            toolRepository.getToolBaseByIdAndVersion(
                id = toolWithDist.baseId,
                version = toolWithDist.baseVersion
            )
        } else {
            flowOf(null)
        }
    }
    val toolViewTemplateFlow = toolRepository.toolViewTemplate

    return isSystemDarkModeFlow.flatMapLatest { isSystemDarkMode ->
        createToolViewStateFlow(
            savedStateHandle = savedStateHandle,
            username = username,
            toolId = toolId,
            isSystemDarkMode = isSystemDarkMode,
            userDataRepository = userDataRepository,
            toolRepository = toolRepository,
            toolStoreRepository = toolStoreRepository,
            toolWithDistFlow = toolWithDistFlow,
            toolBaseWithDistFlow = toolBaseWithDistFlow,
            toolViewTemplateFlow = toolViewTemplateFlow,
            toolViewDataCache = toolViewDataCache
        )
    }
}

private fun createSystemDarkModeFlow(context: Context): Flow<Boolean> = callbackFlow {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, instent: Intent?) {
            context?.let { sendDarkModeState(it) }
        }
    }

    val filter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
    context.registerReceiver(receiver, filter)

    sendDarkModeState(context)

    awaitClose { context.unregisterReceiver(receiver) }
}

private fun SendChannel<Boolean>.sendDarkModeState(context: Context) {
    val configuration = ResourcesHelper.getConfiguration(context)
    val isDarkMode =
        (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    trySend(isDarkMode)
}

private fun createToolViewStateFlow(
    savedStateHandle: SavedStateHandle,
    username: String,
    toolId: String,
    isSystemDarkMode: Boolean,
    userDataRepository: UserDataRepository,
    toolRepository: ToolRepository,
    toolStoreRepository: ToolStoreRepository,
    toolWithDistFlow: Flow<ToolWithDistEntity?>,
    toolBaseWithDistFlow: Flow<ToolBaseWithDistEntity?>,
    toolViewTemplateFlow: Flow<String>,
    toolViewDataCache: MutableStateFlow<ToolViewDataCache?>,
): Flow<ToolViewUiState> = flow {
    userDataRepository.userData.collect { userData ->
        val isDarkMode = determineDarkMode(userData.themeModeConfig, isSystemDarkMode)
        val globalJsVariablesFlow = toolRepository.getGlobalJsVariables(isDarkMode)
        val globalCssVariablesFlow = toolRepository.getGlobalCssVariables(isDarkMode)

        combine(
            toolWithDistFlow,
            toolBaseWithDistFlow,
            toolViewTemplateFlow,
            globalJsVariablesFlow,
            globalCssVariablesFlow,
            ::ToolViewData
        ).collect { (toolWithDist, toolBaseWithDist, toolViewTemplate, globalJsVariables, globalCssVariables) ->
            handleToolViewData(
                savedStateHandle = savedStateHandle,
                username = username,
                toolId = toolId,
                toolWithDist = toolWithDist,
                toolBaseWithDist = toolBaseWithDist,
                toolViewTemplate = toolViewTemplate,
                globalJsVariables = globalJsVariables,
                globalCssVariables = globalCssVariables,
                toolRepository = toolRepository,
                toolStoreRepository = toolStoreRepository,
                toolViewDataCache = toolViewDataCache,
                collector = this
            )
        }
    }
}

private fun determineDarkMode(
    themeModeConfig: ThemeModeConfig,
    isSystemDarkMode: Boolean
): Boolean {
    return when (themeModeConfig) {
        ThemeModeConfig.FollowSystem -> isSystemDarkMode
        ThemeModeConfig.Light -> false
        ThemeModeConfig.Dark -> true
    }
}

private suspend fun handleToolViewData(
    savedStateHandle: SavedStateHandle,
    username: String,
    toolId: String,
    toolWithDist: ToolWithDistEntity?,
    toolBaseWithDist: ToolBaseWithDistEntity?,
    toolViewTemplate: String,
    globalJsVariables: String,
    globalCssVariables: String,
    toolRepository: ToolRepository,
    toolStoreRepository: ToolStoreRepository,
    toolViewDataCache: MutableStateFlow<ToolViewDataCache?>,
    collector: FlowCollector<ToolViewUiState>
) {
    val effectiveToolWithDist = toolWithDist?.takeIf { it.dist.isNotBlank() }
    val effectiveToolBaseWithDist = toolBaseWithDist?.takeIf { it.dist.isNotBlank() }

    if (effectiveToolWithDist != null && effectiveToolBaseWithDist != null) {
        handleNormalMode(
            savedStateHandle = savedStateHandle,
            toolWithDist = effectiveToolWithDist,
            toolBaseWithDist = effectiveToolBaseWithDist,
            toolViewTemplate = toolViewTemplate,
            globalJsVariables = globalJsVariables,
            globalCssVariables = globalCssVariables,
            collector = collector
        )
    } else {
        handlePreviewMode(
            savedStateHandle = savedStateHandle,
            username = username,
            toolId = toolId,
            toolViewTemplate = toolViewTemplate,
            globalJsVariables = globalJsVariables,
            globalCssVariables = globalCssVariables,
            toolRepository = toolRepository,
            toolStoreRepository = toolStoreRepository,
            toolViewDataCache = toolViewDataCache,
            collector = collector
        )
    }
}

private suspend fun handlePreviewMode(
    savedStateHandle: SavedStateHandle,
    username: String,
    toolId: String,
    toolViewTemplate: String,
    globalJsVariables: String,
    globalCssVariables: String,
    toolRepository: ToolRepository,
    toolStoreRepository: ToolStoreRepository,
    toolViewDataCache: MutableStateFlow<ToolViewDataCache?>,
    collector: FlowCollector<ToolViewUiState>
) {
    savedStateHandle[IS_PREVIEW] = true

    val cachedToolViewData = toolViewDataCache.value

    if (cachedToolViewData != null) {
        emitToolResult(
            toolWithDistResult = cachedToolViewData.toolWithDistResult,
            toolBaseWithDistResult = cachedToolViewData.toolBaseWithDistResult,
            toolViewTemplate = toolViewTemplate,
            globalJsVariables = globalJsVariables,
            globalCssVariables = globalCssVariables,
            toolViewDataCache = toolViewDataCache,
            collector = collector
        )
    } else {
        toolStoreRepository.getToolDist(username = username, toolId = toolId)
            .collect { toolWithDistResult ->
                when (toolWithDistResult) {
                    is Result.Success -> {
                        val toolWithDist = toolWithDistResult.data

                        toolRepository.getToolBaseByIdAndVersion(
                            id = toolWithDist.baseId,
                            version = toolWithDist.baseVersion
                        ).collect { localToolBase ->
                            if (localToolBase != null) {
                                emitToolResult(
                                    toolWithDistResult = toolWithDistResult,
                                    toolBaseWithDistResult = Result.Success(localToolBase),
                                    toolViewTemplate = toolViewTemplate,
                                    globalJsVariables = globalJsVariables,
                                    globalCssVariables = globalCssVariables,
                                    toolViewDataCache = toolViewDataCache,
                                    collector = collector
                                )
                            } else {
                                toolStoreRepository.getToolBaseDist(
                                    id = toolWithDist.baseId,
                                    version = toolWithDist.baseVersion
                                ).collect { toolBaseWithDistResult ->
                                    if (toolBaseWithDistResult is Result.Success) {
                                        toolRepository.saveToolBase(toolBaseWithDistResult.data)
                                    }

                                    emitToolResult(
                                        toolWithDistResult = toolWithDistResult,
                                        toolBaseWithDistResult = toolBaseWithDistResult,
                                        toolViewTemplate = toolViewTemplate,
                                        globalJsVariables = globalJsVariables,
                                        globalCssVariables = globalCssVariables,
                                        toolViewDataCache = toolViewDataCache,
                                        collector = collector
                                    )
                                }
                            }
                        }
                    }

                    else -> emitToolResult(
                        toolWithDistResult = toolWithDistResult,
                        toolBaseWithDistResult = Result.Fail("Can not load tool"),
                        toolViewTemplate = toolViewTemplate,
                        globalJsVariables = globalJsVariables,
                        globalCssVariables = globalCssVariables,
                        toolViewDataCache = toolViewDataCache,
                        collector = collector
                    )
                }
            }
    }
}

private suspend fun handleNormalMode(
    savedStateHandle: SavedStateHandle,
    toolWithDist: ToolWithDistEntity,
    toolBaseWithDist: ToolBaseWithDistEntity,
    toolViewTemplate: String,
    globalJsVariables: String,
    globalCssVariables: String,
    collector: FlowCollector<ToolViewUiState>
) {
    savedStateHandle[IS_PREVIEW] = false

    collector.emit(
        createSuccessState(
            toolWithDist = toolWithDist,
            toolBaseWithDist = toolBaseWithDist,
            toolViewTemplate = toolViewTemplate,
            globalJsVariables = globalJsVariables,
            globalCssVariables = globalCssVariables
        )
    )
}

private suspend fun emitToolResult(
    toolWithDistResult: Result<ToolWithDistEntity>,
    toolBaseWithDistResult: Result<ToolBaseWithDistEntity>,
    toolViewTemplate: String,
    globalJsVariables: String,
    globalCssVariables: String,
    toolViewDataCache: MutableStateFlow<ToolViewDataCache?>,
    collector: FlowCollector<ToolViewUiState>
) {
    val uiState = when {
        toolWithDistResult is Result.Success && toolBaseWithDistResult is Result.Success -> {
            toolViewDataCache.value = ToolViewDataCache(
                toolWithDistResult = toolWithDistResult,
                toolBaseWithDistResult = toolBaseWithDistResult
            )
            createSuccessState(
                toolWithDistResult.data,
                toolBaseWithDistResult.data,
                toolViewTemplate,
                globalJsVariables,
                globalCssVariables
            )
        }

        toolWithDistResult is Result.Loading || toolBaseWithDistResult is Result.Loading -> ToolViewUiState.Loading
        else -> {
            if (toolWithDistResult is Result.Error) {
                Timber.e(toolWithDistResult.exception, "Can not load tool")
            } else if (toolWithDistResult is Result.Fail) {
                Timber.w("Failed to load tool: ${toolWithDistResult.message}")
            }

            if (toolBaseWithDistResult is Result.Error) {
                Timber.e(toolBaseWithDistResult.exception, "Can not load tool base")
            } else if (toolBaseWithDistResult is Result.Fail) {
                Timber.w("Failed to load tool base: ${toolBaseWithDistResult.message}")
            }

            ToolViewUiState.Error
        }
    }

    collector.emit(uiState)
}

private fun createSuccessState(
    toolWithDist: ToolWithDistEntity,
    toolBaseWithDist: ToolBaseWithDistEntity,
    toolViewTemplate: String,
    globalJsVariables: String,
    globalCssVariables: String
) = ToolViewUiState.Success(
    toolName = toolWithDist.name,
    htmlData = processHtml(
        toolViewTemplate = toolViewTemplate,
        globalJsVariables = globalJsVariables,
        globalCssVariables = globalCssVariables,
        toolDist = toolWithDist.dist,
        baseDist = toolBaseWithDist.dist
    )
)

private fun createWebViewInstanceState(context: Context): Flow<WebViewInstanceState> = flow {
    val webviewInstance = WebView(context)
    emit(WebViewInstanceState.Success(webviewInstance))
}

sealed interface ToolViewUiState {
    data class Success(
        val toolName: String,
        val htmlData: String
    ) : ToolViewUiState

    data object Error : ToolViewUiState

    data object Loading : ToolViewUiState
}

sealed interface WebViewInstanceState {
    data class Success(
        val webView: WebView
    ) : WebViewInstanceState

    data object Loading : WebViewInstanceState
}

@OptIn(ExperimentalEncodingApi::class)
private fun processHtml(
    toolViewTemplate: String,
    globalJsVariables: String,
    globalCssVariables: String,
    toolDist: String,
    baseDist: String
): String =
    toolViewTemplate
        .replace(oldValue = "{{replace_global_js_variables}}", newValue = globalJsVariables)
        .replace(oldValue = "{{replace_global_css_variables}}", newValue = globalCssVariables)
        .replace(oldValue = "{{replace_dict_code}}", newValue = toolDist)
        .replace(oldValue = "{{replace_base_code}}", newValue = baseDist)

private data class ToolViewDataCache(
    val toolWithDistResult: Result<ToolWithDistEntity>,
    val toolBaseWithDistResult: Result<ToolBaseWithDistEntity>
)

private data class ToolViewData(
    val toolWithDist: ToolWithDistEntity?,
    val toolBaseWithDist: ToolBaseWithDistEntity?,
    val template: String,
    val jsVariables: String,
    val cssVariables: String
)

private const val IS_PREVIEW = "IS_PREVIEW"
