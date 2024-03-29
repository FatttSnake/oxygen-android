package top.fatweb.oxygen.toolbox.ui.tool

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import top.fatweb.oxygen.toolbox.model.tool.ToolGroup
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ToolsScreenViewModel @Inject constructor(
    toolRepository: ToolRepository
) : ViewModel() {
    val toolsScreenUiState: StateFlow<ToolsScreenUiState> =
        toolRepository.toolGroups
            .map {
                ToolsScreenUiState.Success(it)
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = ToolsScreenUiState.Loading,
                started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
            )
}

sealed interface ToolsScreenUiState {
    data object Loading : ToolsScreenUiState
    data class Success(val toolGroups: List<ToolGroup>) : ToolsScreenUiState
}