package top.fatweb.oxygen.toolbox.ui.star

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class StarScreenViewModel @Inject constructor(
    private val toolRepository: ToolRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val searchValue = savedStateHandle.getStateFlow(SEARCH_VALUE, "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val starScreenUiState: StateFlow<StarScreenUiState> =
        searchValue.flatMapLatest { searchValue ->
            toolRepository.getStarToolsStream(searchValue).map {
                if (it.isEmpty()) {
                    StarScreenUiState.Nothing
                } else {
                    StarScreenUiState.Success(it)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = StarScreenUiState.Loading,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
        )

    fun onSearchValueChange(value: String) {
        savedStateHandle[SEARCH_VALUE] = value
    }

    fun unstar(tool: ToolEntity) {
        viewModelScope.launch {
            toolRepository.updateTool(tool.copy(isStar = false))
        }
    }
}

sealed interface StarScreenUiState {
    data object Loading : StarScreenUiState
    data object Nothing : StarScreenUiState
    data class Success(val tools: List<ToolEntity>) : StarScreenUiState
}

private const val SEARCH_VALUE = "searchValue"