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

    fun changeInstallInfo(
        status: ToolStoreUiState.InstallInfo.Status = installInfo.value.status,
        type: ToolStoreUiState.InstallInfo.Type = installInfo.value.type
    ) {
        savedStateHandle[INSTALL_INFO] = ToolStoreUiState.InstallInfo(status, type)
    }

    fun installTool(
        toolEntity: ToolEntity
    ) {
        viewModelScope.launch {
            storeRepository.detail(toolEntity.authorUsername, toolEntity.toolId).collect { result ->
                when (result) {
                    Result.Loading -> changeInstallInfo(status = ToolStoreUiState.InstallInfo.Status.Processing)

                    is Result.Error, is Result.Fail -> changeInstallInfo(status = ToolStoreUiState.InstallInfo.Status.Fail)

                    is Result.Success -> {
                        when (installInfo.value.type) {
                            ToolStoreUiState.InstallInfo.Type.Install -> toolRepository.saveTool(
                                result.data
                            )

                            ToolStoreUiState.InstallInfo.Type.Upgrade -> {
                                toolRepository.removeTool(toolEntity)
                                toolRepository.saveTool(result.data)
                            }
                        }

                        changeInstallInfo(status = ToolStoreUiState.InstallInfo.Status.Success)
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
        var status: Status = Status.None,
        var type: Type = Type.Install
    ) : Parcelable {
        enum class Status {
            None, Pending, Processing, Success, Fail
        }

        enum class Type {
            Install, Upgrade
        }
    }
}

private const val SEARCH_VALUE = "searchValue"
private const val CURRENT_PAGE = "currentPage"
private const val INSTALL_INFO = "installInfo"
