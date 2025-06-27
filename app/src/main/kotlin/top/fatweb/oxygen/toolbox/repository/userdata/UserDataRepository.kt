package top.fatweb.oxygen.toolbox.repository.userdata

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeTypeConfig
import top.fatweb.oxygen.toolbox.model.userdata.UserData

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setLanguageConfig(languageConfig: LanguageConfig)

    suspend fun setLaunchPageConfig(launchPageConfig: LaunchPageConfig)

    suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig)

    suspend fun setThemeTypeConfig(themeTypeConfig: ThemeTypeConfig)

    suspend fun setUseDynamicColor(useDynamicColor: Boolean)

    suspend fun updateIsNotFirstLaunch()
}
