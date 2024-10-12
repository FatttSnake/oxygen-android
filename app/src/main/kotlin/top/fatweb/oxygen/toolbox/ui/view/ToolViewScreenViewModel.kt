package top.fatweb.oxygen.toolbox.ui.view

import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.navigation.ToolViewArgs
import top.fatweb.oxygen.toolbox.repository.tool.StoreRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.util.decodeToStringWithZip
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ToolViewScreenViewModel @Inject constructor(
    @ApplicationContext context: Context,
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
        savedStateHandle = savedStateHandle,
        username = username,
        toolId = toolId,
        preview = preview,
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
    }.stateIn(
        viewModelScope,
        initialValue = WebViewInstanceState.Loading,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds)
    )
}

private fun toolViewUiState(
    savedStateHandle: SavedStateHandle,
    username: String,
    toolId: String,
    preview: Boolean,
    storeRepository: StoreRepository,
    toolRepository: ToolRepository,
    storeDetailCache: MutableStateFlow<Result<ToolEntity>?>
): Flow<ToolViewUiState> {
    val toolViewTemplate = toolRepository.toolViewTemplate
    val entityFlow =
        if (!preview) toolRepository.getToolByUsernameAndToolId(username, toolId) else flowOf(null)

    return flow {
        combine(entityFlow, toolViewTemplate, ::Pair).collect { (entityFlow, toolViewTemplate) ->
            if (entityFlow == null) {
                savedStateHandle[IS_PREVIEW] = true
                val cachedDetail = storeDetailCache.value
                if (cachedDetail != null) {
                    emitResult(result = cachedDetail, toolViewTemplate = toolViewTemplate)
                } else {
                    storeRepository.detail(username, toolId).collect { result ->
                        storeDetailCache.value = result
                        emitResult(result = result, toolViewTemplate = toolViewTemplate)
                    }
                }
            } else {
                savedStateHandle[IS_PREVIEW] = false
                emit(
                    ToolViewUiState.Success(
                        entityFlow.name,
                        processHtml(
                            toolViewTemplate = toolViewTemplate,
                            distBase64 = entityFlow.dist!!,
                            baseBase64 = entityFlow.base!!
                        )
                    )
                )
            }
        }
    }
}

private suspend fun FlowCollector<ToolViewUiState>.emitResult(
    result: Result<ToolEntity>,
    toolViewTemplate: String
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
private fun processHtml(toolViewTemplate: String, distBase64: String, baseBase64: String): String {
    val dist = Base64.decodeToStringWithZip(distBase64)
    val base = Base64.decodeToStringWithZip(baseBase64)

    return toolViewTemplate
        .replace(oldValue = "{{replace_dict_code}}", newValue = dist)
        .replace(oldValue = "{{replace_base_code}}", newValue = base)
}

private const val IS_PREVIEW = "IS_PREVIEW"
