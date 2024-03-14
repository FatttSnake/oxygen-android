package top.fatweb.oxygen.toolbox.repository

import kotlinx.coroutines.flow.Flow
import top.fatweb.oxygen.toolbox.model.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.LanguageConfig
import top.fatweb.oxygen.toolbox.model.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.UserData

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setLanguageConfig(languageConfig: LanguageConfig)

    suspend fun setLaunchPageConfig(launchPageConfig: LaunchPageConfig)

    suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig)

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    suspend fun setUseDynamicColor(useDynamicColor: Boolean)
}