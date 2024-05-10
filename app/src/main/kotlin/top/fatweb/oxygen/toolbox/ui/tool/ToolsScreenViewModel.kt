package top.fatweb.oxygen.toolbox.ui.tool

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import top.fatweb.oxygen.toolbox.model.Page
import top.fatweb.oxygen.toolbox.model.tool.Tool
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject

@HiltViewModel
class ToolsScreenViewModel @Inject constructor(
    private val toolRepository: ToolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val searchValue = savedStateHandle.getStateFlow(SEARCH_VALUE, "")
    private val currentPage = savedStateHandle.getStateFlow(CURRENT_PAGE, 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val storeData: Flow<PagingData<Tool>> = combine(
        searchValue,
        currentPage,
        ::Pair
    ).flatMapLatest { (searchValue, currentPage) ->
        toolRepository.getStore(searchValue, currentPage).cachedIn(viewModelScope)
    }
}

sealed interface ToolsScreenUiState {
    data object Loading : ToolsScreenUiState
    data class Success(val tools: Page<Tool>) : ToolsScreenUiState
}

private const val SEARCH_VALUE = "searchValue"
private const val CURRENT_PAGE = "currentPage"