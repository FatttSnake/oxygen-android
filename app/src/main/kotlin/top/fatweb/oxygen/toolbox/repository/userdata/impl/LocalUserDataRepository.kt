package top.fatweb.oxygen.toolbox.repository.userdata.impl

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.data.userdata.PreferencesDataSource
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeTypeConfig
import top.fatweb.oxygen.toolbox.model.userdata.UserData
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository
import javax.inject.Inject

internal class LocalUserDataRepository @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> =
        preferencesDataSource.userData

    override suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        preferencesDataSource.setLanguageConfig(languageConfig)
    }

    override suspend fun setLaunchPageConfig(launchPageConfig: LaunchPageConfig) {
        preferencesDataSource.setLaunchPageConfig(launchPageConfig)
    }

    override suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig) {
        preferencesDataSource.setThemeBrandConfig(themeBrandConfig)
    }

    override suspend fun setThemeTypeConfig(themeTypeConfig: ThemeTypeConfig) {
        preferencesDataSource.setThemeTypeConfig(themeTypeConfig)
    }

    override suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        preferencesDataSource.setUseDynamicColor(useDynamicColor)
    }

    override suspend fun updateIsNotFirstLaunch() {
        preferencesDataSource.updateIsNotFirstLaunch()
    }
}
