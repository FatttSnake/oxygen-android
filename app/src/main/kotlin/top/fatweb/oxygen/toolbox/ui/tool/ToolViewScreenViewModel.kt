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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.navigation.ToolViewArgs
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.util.decodeToStringWithZip
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ToolViewScreenViewModel @Inject constructor(
    toolRepository: ToolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val toolViewArgs = ToolViewArgs(savedStateHandle)
    val username = toolViewArgs.username
    val toolId = toolViewArgs.toolId

    val toolViewUiState: StateFlow<ToolViewUiState> = toolViewUiState(
        username = username,
        toolId = toolId,
        toolRepository = toolRepository
    )
        .stateIn(
            scope = viewModelScope,
            initialValue = ToolViewUiState.Loading,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
        )
}

private fun toolViewUiState(
    username: String,
    toolId: String,
    toolRepository: ToolRepository
): Flow<ToolViewUiState> {
    val result = toolRepository.detail(
        username = username,
        toolId = toolId
    )
    val toolViewTemplate = toolRepository.toolViewTemplate

    return combine(result, toolViewTemplate, ::Pair).map { (result, toolViewTemplate) ->
        when (result) {
            is Result.Success -> {
                val dist = result.data.dist!!
                val base = result.data.base!!
                ToolViewUiState.Success(
                    processHtml(
                        toolViewTemplate = toolViewTemplate,
                        distBase64 = dist,
                        baseBase64 = base
                    )
                )
            }

            is Result.Loading -> ToolViewUiState.Loading
            is Result.Error -> {
                Log.e("TAG", "toolViewUiState: can not load tool", result.exception)

                ToolViewUiState.Error
            }

            is Result.Fail -> ToolViewUiState.Error
        }
    }
}

sealed interface ToolViewUiState {
    data class Success(val htmlData: String) : ToolViewUiState

    data object Error : ToolViewUiState

    data object Loading : ToolViewUiState
}

@OptIn(ExperimentalEncodingApi::class)
fun processHtml(toolViewTemplate: String, distBase64: String, baseBase64: String): String {
    val dist = Base64.decodeToStringWithZip(distBase64)
    val base = Base64.decodeToStringWithZip(baseBase64)

    return toolViewTemplate.replace("{{replace_code}}", "$dist\n$base")
}
