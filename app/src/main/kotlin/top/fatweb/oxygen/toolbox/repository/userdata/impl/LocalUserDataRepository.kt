package top.fatweb.oxygen.toolbox.repository.userdata.impl

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.data.userdata.OxygenPreferencesDataSource
import top.fatweb.oxygen.toolbox.model.userdata.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.userdata.UserData
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository
import javax.inject.Inject

internal class LocalUserDataRepository @Inject constructor(
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
