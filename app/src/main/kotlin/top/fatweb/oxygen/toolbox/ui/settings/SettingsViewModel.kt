package top.fatweb.oxygen.toolbox.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import top.fatweb.oxygen.toolbox.model.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.LanguageConfig
import top.fatweb.oxygen.toolbox.model.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.repository.UserDataRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map { userData ->
                SettingsUiState.Success(
                    settings = UserEditableSettings(
                        languageConfig = userData.languageConfig,
                        launchPageConfig = userData.launchPageConfig,
                        themeBrandConfig = userData.themeBrandConfig,
                        darkThemeConfig = userData.darkThemeConfig,
                        useDynamicColor = userData.useDynamicColor
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = SettingsUiState.Loading,
                started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
            )

    fun updateLanguageConfig(languageConfig: LanguageConfig) {
        viewModelScope.launch {
            userDataRepository.setLanguageConfig(languageConfig)
        }
    }

    fun updateLaunchPageConfig(launchPageConfig: LaunchPageConfig) {
        viewModelScope.launch {
            userDataRepository.setLaunchPageConfig(launchPageConfig)
        }
    }

    fun updateThemeBrandConfig(themeBrandConfig: ThemeBrandConfig) {
        viewModelScope.launch {
            userDataRepository.setThemeBrandConfig(themeBrandConfig)
        }
    }

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun updateUseDynamicColor(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userDataRepository.setUseDynamicColor(useDynamicColor)
        }
    }
}

data class UserEditableSettings(
    val languageConfig: LanguageConfig,
    val launchPageConfig: LaunchPageConfig,
    val themeBrandConfig: ThemeBrandConfig,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean
)

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}