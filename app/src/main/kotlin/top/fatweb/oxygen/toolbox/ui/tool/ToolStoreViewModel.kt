package top.fatweb.oxygen.toolbox.ui.tool

import android.os.Parcelable
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
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.repository.tool.StoreRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import javax.inject.Inject

@HiltViewModel
class ToolStoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val toolRepository: ToolRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val searchValue = savedStateHandle.getStateFlow(SEARCH_VALUE, "")
    private val currentPage = savedStateHandle.getStateFlow(CURRENT_PAGE, 1)
    val installInfo = savedStateHandle.getStateFlow(INSTALL_INFO, ToolStoreUiState.InstallInfo())

    @OptIn(ExperimentalCoroutinesApi::class)
    val storeData: Flow<PagingData<ToolEntity>> = combine(
        searchValue, currentPage, ::Pair
    ).flatMapLatest { (searchValue, currentPage) ->
        storeRepository.getStore(searchValue, currentPage).cachedIn(
            scope = viewModelScope
        )
    }

    fun changeInstallStatus(installStatus: ToolStoreUiState.Status) {
        savedStateHandle[INSTALL_INFO] = ToolStoreUiState.InstallInfo(installStatus)
    }

    fun installTool(username: String, toolId: String) {
        viewModelScope.launch {
            storeRepository.detail(username, toolId).collect {
                when (it) {
                    Result.Loading -> savedStateHandle[INSTALL_INFO] =
                        ToolStoreUiState.InstallInfo(ToolStoreUiState.Status.Installing)

                    is Result.Error, is Result.Fail -> savedStateHandle[INSTALL_INFO] =
                        ToolStoreUiState.InstallInfo(ToolStoreUiState.Status.Fail)

                    is Result.Success -> {
                        toolRepository.saveTool(it.data)
                        savedStateHandle[INSTALL_INFO] =
                            ToolStoreUiState.InstallInfo(ToolStoreUiState.Status.Success)
                    }
                }
            }
        }
    }
}

@Parcelize
data class ToolStoreUiState(
    val installInfo: InstallInfo
) : Parcelable {
    @Parcelize
    data class InstallInfo(
        var status: Status = Status.None
    ) : Parcelable

    enum class Status {
        None, Pending, Installing, Success, Fail
    }
}

private const val SEARCH_VALUE = "searchValue"
private const val CURRENT_PAGE = "currentPage"
private const val INSTALL_INFO = "installInfo"
