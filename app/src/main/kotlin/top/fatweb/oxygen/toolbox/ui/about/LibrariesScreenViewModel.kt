package top.fatweb.oxygen.toolbox.ui.about

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import top.fatweb.oxygen.toolbox.model.lib.Dependencies
import top.fatweb.oxygen.toolbox.repository.lib.DepRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LibrariesScreenViewModel @Inject constructor(
    private val depRepository: DepRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val searchValue = savedStateHandle.getStateFlow(SEARCH_VALUE, "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val librariesScreenUiState: StateFlow<LibrariesScreenUiState> =
        depRepository.getSearchNameCount()
            .flatMapLatest {totalCount ->
                if (totalCount < SEARCH_MIN_COUNT) {
                    flowOf(LibrariesScreenUiState.Nothing)
                } else {
                    searchValue.flatMapLatest {value ->
                        depRepository.searchName(value).map {
                            if (it.libraries.isEmpty()) {
                                LibrariesScreenUiState.NotFound
                            } else {
                                LibrariesScreenUiState.Success(it)
                            }
                        }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = LibrariesScreenUiState.Loading,
                started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
            )

    fun onSearchValueChange(value: String) {
        savedStateHandle[SEARCH_VALUE] = value
    }
}

sealed interface LibrariesScreenUiState {
    data object Loading: LibrariesScreenUiState

    data object Nothing: LibrariesScreenUiState

    data object NotFound: LibrariesScreenUiState

    data class Success(val dependencies: Dependencies) : LibrariesScreenUiState
}

private const val SEARCH_MIN_COUNT = 1
private const val SEARCH_VALUE = "searchValue"
