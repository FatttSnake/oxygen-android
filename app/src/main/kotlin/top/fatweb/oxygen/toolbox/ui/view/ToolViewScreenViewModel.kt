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
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.model.userdata.ThemeTypeConfig
import top.fatweb.oxygen.toolbox.navigation.ToolViewArgs
import top.fatweb.oxygen.toolbox.repository.tool.StoreRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository
import top.fatweb.oxygen.toolbox.ui.util.ResourcesUtils
import top.fatweb.oxygen.toolbox.util.decodeToStringWithZip
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ToolViewScreenViewModel @Inject constructor(
    @ApplicationContext context: Context,
    userDataRepository: UserDataRepository,
    storeRepository: StoreRepository,
    toolRepository: ToolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isPreview = savedStateHandle.getStateFlow(IS_PREVIEW, false)

    private val toolViewArgs = ToolViewArgs(savedStateHandle)
    private val username = toolViewArgs.username
    private val toolId = toolViewArgs.toolId
    private val preview = toolViewArgs.preview

    private val storeDetailCache = MutableStateFlow<Result<ToolEntity>?>(null)


    val toolViewUiState: StateFlow<ToolViewUiState> = toolViewUiState(
        context = context,
        savedStateHandle = savedStateHandle,
        username = username,
        toolId = toolId,
        preview = preview,
        userDataRepository = userDataRepository,
        storeRepository = storeRepository,
        toolRepository = toolRepository,
        storeDetailCache = storeDetailCache
    )
        .stateIn(
            scope = viewModelScope,
            initialValue = ToolViewUiState.Loading,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds)
        )

    val webviewInstance = flow<WebViewInstanceState> {
        val webviewInstance = WebView(context)
        emit(WebViewInstanceState.Success(webviewInstance))
    }
        .stateIn(
            viewModelScope,
            initialValue = WebViewInstanceState.Loading,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds)
        )
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun toolViewUiState(
    context: Context,
    savedStateHandle: SavedStateHandle,
    username: String,
    toolId: String,
    preview: Boolean,
    userDataRepository: UserDataRepository,
    storeRepository: StoreRepository,
    toolRepository: ToolRepository,
    storeDetailCache: MutableStateFlow<Result<ToolEntity>?>
): Flow<ToolViewUiState> {
    val toolViewTemplate = toolRepository.toolViewTemplate
    val entityFlow =
        if (!preview) toolRepository.getToolByUsernameAndToolId(username, toolId) else flowOf(null)

    val isSystemDarkModeFlow = callbackFlow<Boolean> {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, instent: Intent?) {
                context?.let(ResourcesUtils::getConfiguration)?.run {
                    trySend((uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        context.registerReceiver(receiver, filter)

        trySend((ResourcesUtils.getConfiguration(context).uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)

        awaitClose { context.unregisterReceiver(receiver) }
    }

    return isSystemDarkModeFlow.flatMapLatest { isSystemDarkMode ->
        flow {
            userDataRepository.userData.collect { userData ->
                val isDarkMode: Boolean = when (userData.themeTypeConfig) {
                    ThemeTypeConfig.FollowSystem -> isSystemDarkMode
                    ThemeTypeConfig.Light -> false
                    ThemeTypeConfig.Dark -> true
                }
                val globalJsVariables = toolRepository.getGlobalJsVariables(isDarkMode)
                val globalCssVariables = toolRepository.getGlobalCssVariables(isDarkMode)
                combine(entityFlow, toolViewTemplate, globalJsVariables, ::Triple)
                    .combine(globalCssVariables) { triple, globalCssVariables ->
                        Quadruple(triple.first, triple.second, triple.third, globalCssVariables)
                    }
                    .collect { (entityFlow, toolViewTemplate, globalJsVariables, globalCssVariables) ->
                        if (entityFlow == null) {
                            savedStateHandle[IS_PREVIEW] = true
                            val cachedDetail = storeDetailCache.value
                            if (cachedDetail != null) {
                                emitResult(
                                    result = cachedDetail,
                                    toolViewTemplate = toolViewTemplate,
                                    globalJsVariables = globalJsVariables,
                                    globalCssVariables = globalCssVariables
                                )
                            } else {
                                storeRepository.detail(username, toolId).collect { result ->
                                    storeDetailCache.value = result
                                    emitResult(
                                        result = result,
                                        toolViewTemplate = toolViewTemplate,
                                        globalJsVariables = globalJsVariables,
                                        globalCssVariables = globalCssVariables
                                    )
                                }
                            }
                        } else {
                            savedStateHandle[IS_PREVIEW] = false
                            emit(
                                ToolViewUiState.Success(
                                    entityFlow.name,
                                    processHtml(
                                        toolViewTemplate = toolViewTemplate,
                                        globalJsVariables = globalJsVariables,
                                        globalCssVariables = globalCssVariables,
                                        distBase64 = entityFlow.dist!!,
                                        baseBase64 = entityFlow.base!!
                                    )
                                )
                            )
                        }
                    }
            }
        }
    }
}

private suspend fun FlowCollector<ToolViewUiState>.emitResult(
    result: Result<ToolEntity>,
    toolViewTemplate: String,
    globalJsVariables: String,
    globalCssVariables: String
) {
    emit(
        when (result) {
            is Result.Success -> {
                val dist = result.data.dist!!
                val base = result.data.base!!
                ToolViewUiState.Success(
                    result.data.name,
                    processHtml(
                        toolViewTemplate = toolViewTemplate,
                        globalJsVariables = globalJsVariables,
                        globalCssVariables = globalCssVariables,
                        distBase64 = dist,
                        baseBase64 = base
                    )
                )
            }

            is Result.Loading -> ToolViewUiState.Loading
            is Result.Error -> {
                Timber.e(result.exception, "Can not load tool")
                ToolViewUiState.Error
            }

            is Result.Fail -> ToolViewUiState.Error
        }
    )
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
    distBase64: String,
    baseBase64: String
): String {
    val dist = Base64.decodeToStringWithZip(distBase64)
    val base = Base64.decodeToStringWithZip(baseBase64)

    return toolViewTemplate
        .replace(oldValue = "{{replace_global_js_variables}}", newValue = globalJsVariables)
        .replace(oldValue = "{{replace_global_css_variables}}", newValue = globalCssVariables)
        .replace(oldValue = "{{replace_dict_code}}", newValue = dist)
        .replace(oldValue = "{{replace_base_code}}", newValue = base)
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private const val IS_PREVIEW = "IS_PREVIEW"
