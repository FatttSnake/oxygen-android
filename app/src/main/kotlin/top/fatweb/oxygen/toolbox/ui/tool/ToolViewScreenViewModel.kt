package top.fatweb.oxygen.toolbox.ui.tool

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import top.fatweb.oxygen.toolbox.model.Result
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
    storeRepository: StoreRepository,
    toolRepository: ToolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isPreview = savedStateHandle.getStateFlow(IS_PREVIEW, false)

    private val toolViewArgs = ToolViewArgs(savedStateHandle)
    private val username = toolViewArgs.username
    private val toolId = toolViewArgs.toolId

    val toolViewUiState: StateFlow<ToolViewUiState> = toolViewUiState(
        savedStateHandle = savedStateHandle,
        username = username,
        toolId = toolId,
        storeRepository = storeRepository,
        toolRepository = toolRepository
    )
        .stateIn(
            scope = viewModelScope,
            initialValue = ToolViewUiState.Loading,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
        )
}

private fun toolViewUiState(
    savedStateHandle: SavedStateHandle,
    username: String,
    toolId: String,
    storeRepository: StoreRepository,
    toolRepository: ToolRepository
): Flow<ToolViewUiState> {
    val toolViewTemplate = toolRepository.toolViewTemplate
    val entityFlow = toolRepository.getToolByUsernameAndToolId(username, toolId)

    return flow {
        combine(entityFlow, toolViewTemplate, ::Pair).collect { (entityFlow, toolViewTemplate) ->
            if (entityFlow == null) {
                savedStateHandle[IS_PREVIEW] = true
                storeRepository.detail(username, toolId).collect {
                    emit(
                        when (it) {
                            is Result.Success -> {
                                val dist = it.data.dist!!
                                val base = it.data.base!!
                                ToolViewUiState.Success(
                                    it.data.name,
                                    processHtml(
                                        toolViewTemplate = toolViewTemplate,
                                        distBase64 = dist,
                                        baseBase64 = base
                                    )
                                )
                            }

                            is Result.Loading -> ToolViewUiState.Loading
                            is Result.Error -> {
                                Log.e("TAG", "toolViewUiState: can not load tool", it.exception)

                                ToolViewUiState.Error
                            }

                            is Result.Fail -> ToolViewUiState.Error
                        }
                    )
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

sealed interface ToolViewUiState {
    data class Success(
        val toolName: String,
        val htmlData: String
    ) : ToolViewUiState

    data object Error : ToolViewUiState

    data object Loading : ToolViewUiState
}

@OptIn(ExperimentalEncodingApi::class)
private fun processHtml(toolViewTemplate: String, distBase64: String, baseBase64: String): String {
    val dist = Base64.decodeToStringWithZip(distBase64)
    val base = Base64.decodeToStringWithZip(baseBase64)

    return toolViewTemplate.replace("{{replace_code}}", "$dist\n$base")
}

private const val IS_PREVIEW = "IS_PREVIEW"
