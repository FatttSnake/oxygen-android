package top.fatweb.oxygen.toolbox.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import top.fatweb.oxygen.toolbox.model.lib.Dependencies
import top.fatweb.oxygen.toolbox.repository.lib.DepRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LibrariesScreenViewModel @Inject constructor(
    depRepository: DepRepository
) : ViewModel() {
    val librariesScreenUiState: StateFlow<LibrariesScreenUiState> =
        depRepository.dependencies
            .map {
                LibrariesScreenUiState.Success(it)
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = LibrariesScreenUiState.Loading,
                started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
            )
}

sealed interface LibrariesScreenUiState {
    data object Loading: LibrariesScreenUiState

    data class Success(val dependencies: Dependencies) : LibrariesScreenUiState
}
