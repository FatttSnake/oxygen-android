package top.fatweb.oxygen.toolbox.ui.store

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import top.fatweb.oxygen.toolbox.model.Result
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.repository.tool.ToolStoreRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ToolStoreViewModel @Inject constructor(
    private val toolStoreRepository: ToolStoreRepository,
    private val toolRepository: ToolRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val searchValue = savedStateHandle.getStateFlow(SEARCH_VALUE, "")
    val installInfo = savedStateHandle.getStateFlow(INSTALL_INFO, ToolStoreUiState.InstallInfo())

    @OptIn(ExperimentalCoroutinesApi::class)
    val storeData: Flow<PagingData<ToolEntity>> = searchValue
        .flatMapLatest { searchValue ->
            toolStoreRepository
                .getStore(searchValue)
                .cachedIn(viewModelScope)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = PagingData.empty(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds)
        )

    fun onSearchValueChange(value: String) {
        savedStateHandle[SEARCH_VALUE] = value
    }

    fun changeInstallInfo(
        status: ToolStoreUiState.InstallInfo.Status = installInfo.value.status,
        type: ToolStoreUiState.InstallInfo.Type = installInfo.value.type
    ) {
        savedStateHandle[INSTALL_INFO] = ToolStoreUiState.InstallInfo(status, type)
    }

    fun installTool(
        toolEntity: ToolEntity,
        onFinish: () -> Unit
    ) {
        viewModelScope.launch {
            toolStoreRepository.getToolDist(
                username = toolEntity.authorUsername,
                toolId = toolEntity.toolId
            ).collect { result ->
                when (result) {
                    Result.Loading -> changeInstallInfo(status = ToolStoreUiState.InstallInfo.Status.Processing)

                    is Result.Error -> {
                        Timber.e(
                            result.exception,
                            "Failed to install tool: ${toolEntity.authorUsername}:${toolEntity.toolId}"
                        )
                        changeInstallInfo(status = ToolStoreUiState.InstallInfo.Status.Fail)
                    }

                    is Result.Fail -> {
                        Timber.w("Failed to install tool base: ${toolEntity.authorUsername}:${toolEntity.toolId}, reason: ${result.message}")
                        changeInstallInfo(status = ToolStoreUiState.InstallInfo.Status.Fail)
                    }

                    is Result.Success -> {
                        if (!installToolBase(
                                baseId = result.data.baseId,
                                baseVersion = result.data.baseVersion
                            )
                        ) {
                            changeInstallInfo(status = ToolStoreUiState.InstallInfo.Status.Fail)
                            return@collect
                        }

                        when (installInfo.value.type) {
                            ToolStoreUiState.InstallInfo.Type.Install -> {
                                toolRepository.saveTool(
                                    result.data
                                )
                                toolEntity.installedVersion = result.data.ver
                            }

                            ToolStoreUiState.InstallInfo.Type.Upgrade -> {
                                toolRepository.removeTool(
                                    toolEntity.authorUsername,
                                    toolEntity.toolId
                                )
                                toolRepository.saveTool(result.data)
                                toolEntity.installedVersion = result.data.ver
                            }
                        }

                        changeInstallInfo(status = ToolStoreUiState.InstallInfo.Status.Success)
                        onFinish()
                    }
                }
            }
        }
    }

    private suspend fun installToolBase(
        baseId: Long,
        baseVersion: Long
    ): Boolean {
        val localToolBase = toolRepository.getToolBaseByIdAndVersion(
            id = baseId,
            version = baseVersion
        ).first()

        return when {
            localToolBase != null && !localToolBase.isCache -> true
            localToolBase != null && localToolBase.isCache -> {
                toolRepository.updateToolBase(localToolBase.apply {
                    isCache = false
                })
                true
            }

            else -> {
                var isInstallSuccess = false
                var shouldContinue = true

                toolStoreRepository.getToolBaseDist(
                    id = baseId,
                    version = baseVersion
                )
                    .collect { result ->
                        if (!shouldContinue) return@collect

                        when (result) {
                            is Result.Success -> {
                                toolRepository.saveToolBase(result.data.apply {
                                    isCache = false
                                })
                                isInstallSuccess = true
                                shouldContinue = false
                            }

                            is Result.Error -> {
                                Timber.e(
                                    result.exception,
                                    "Failed to install tool base: $baseId version $baseVersion"
                                )
                                shouldContinue = false
                            }

                            is Result.Fail -> {
                                Timber.w("Failed to install tool base: $baseId version: $baseVersion, reason: ${result.message}")
                                shouldContinue = false
                            }

                            Result.Loading -> {}
                        }
                    }

                isInstallSuccess
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
private const val INSTALL_INFO = "installInfo"
