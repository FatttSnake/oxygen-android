package top.fatweb.oxygen.toolbox.repository

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.datastore.OxygenPreferencesDataSource
import top.fatweb.oxygen.toolbox.model.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.LanguageConfig
import top.fatweb.oxygen.toolbox.model.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.UserData
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val oxygenPreferencesDataSource: OxygenPreferencesDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> =
        oxygenPreferencesDataSource.userData

    override suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        oxygenPreferencesDataSource.setLanguageConfig(languageConfig)
    }

    override suspend fun setLaunchPageConfig(launchPageConfig: LaunchPageConfig) {
        oxygenPreferencesDataSource.setLaunchPageConfig(launchPageConfig)
    }

    override suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig) {
        oxygenPreferencesDataSource.setThemeBrandConfig(themeBrandConfig)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        oxygenPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        oxygenPreferencesDataSource.setUseDynamicColor(useDynamicColor)
    }
}