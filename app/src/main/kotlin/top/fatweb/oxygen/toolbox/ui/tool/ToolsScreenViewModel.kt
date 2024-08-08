package top.fatweb.oxygen.toolbox.ui.tool

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.repository.tool.StoreRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ToolsScreenViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    toolRepository: ToolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val searchValue = savedStateHandle.getStateFlow(SEARCH_VALUE, "")

    val toolsScreenUiState: StateFlow<ToolsScreenUiState> =
        toolRepository.getAllToolsStream()
            .map {
                if (it.isEmpty()) {
                    ToolsScreenUiState.Nothing
                } else {
                    ToolsScreenUiState.Success(it)
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = ToolsScreenUiState.Loading,
                started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
            )

}

sealed interface ToolsScreenUiState {
    data object Loading : ToolsScreenUiState
    data object Nothing : ToolsScreenUiState
    data class Success(val tools: List<ToolEntity>) : ToolsScreenUiState
}

private const val SEARCH_VALUE = "searchValue"
