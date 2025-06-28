package top.fatweb.oxygen.toolbox.data.userdata

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import top.fatweb.oxygen.toolbox.data.LanguageConfigProto
import top.fatweb.oxygen.toolbox.data.LaunchPageConfigProto
import top.fatweb.oxygen.toolbox.data.ThemeBrandConfigProto
import top.fatweb.oxygen.toolbox.data.ThemeModeConfigProto
import top.fatweb.oxygen.toolbox.data.UserPreferences
import top.fatweb.oxygen.toolbox.data.copy
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeModeConfig
import top.fatweb.oxygen.toolbox.model.userdata.UserData
import javax.inject.Inject

class PreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userData = userPreferences.data
        .map {
            UserData(
                languageConfig = when (it.languageConfig) {
                    null,
                    LanguageConfigProto.UNRECOGNIZED,
                    LanguageConfigProto.LANGUAGE_CONFIG_UNSPECIFIED,
                    LanguageConfigProto.LANGUAGE_CONFIG_FOLLOW_SYSTEM
                        -> LanguageConfig.FollowSystem

                    LanguageConfigProto.LANGUAGE_CONFIG_CHINESE
                        -> LanguageConfig.Chinese

                    LanguageConfigProto.LANGUAGE_CONFIG_ENGLISH
                        -> LanguageConfig.English
                },
                launchPageConfig = when (it.launchPageConfig) {
                    null,
                    LaunchPageConfigProto.UNRECOGNIZED,
                    LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_UNSPECIFIED,
                    LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_TOOLS
                        -> LaunchPageConfig.Tools

                    LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_STAR
                        -> LaunchPageConfig.Star
                },
                themeBrandConfig = when (it.themeBrandConfig) {
                    null,
                    ThemeBrandConfigProto.UNRECOGNIZED,
                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_UNSPECIFIED,
                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_DEFAULT
                        ->
                        ThemeBrandConfig.Default

                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_ANDROID
                        -> ThemeBrandConfig.Android
                },
                themeModeConfig = when (it.themeModeConfig) {
                    null,
                    ThemeModeConfigProto.UNRECOGNIZED,
                    ThemeModeConfigProto.THEME_MODE_CONFIG_UNSPECIFIED,
                    ThemeModeConfigProto.THEME_MODE_CONFIG_FOLLOW_SYSTEM
                        ->
                        ThemeModeConfig.FollowSystem

                    ThemeModeConfigProto.THEME_MODE_CONFIG_LIGHT
                        -> ThemeModeConfig.Light

                    ThemeModeConfigProto.THEME_MODE_CONFIG_DARK
                        -> ThemeModeConfig.Dark
                },
                useDynamicColor = it.useDynamicColor,
                isNotFirstLaunch = it.isNotFirstLaunch
            )
        }

    suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        userPreferences.updateData {
            it.copy {
                this.languageConfig = when (languageConfig) {
                    LanguageConfig.FollowSystem -> LanguageConfigProto.LANGUAGE_CONFIG_FOLLOW_SYSTEM
                    LanguageConfig.Chinese -> LanguageConfigProto.LANGUAGE_CONFIG_CHINESE
                    LanguageConfig.English -> LanguageConfigProto.LANGUAGE_CONFIG_ENGLISH
                }
            }
        }
    }

    suspend fun setLaunchPageConfig(launchPageConfig: LaunchPageConfig) {
        userPreferences.updateData {
            it.copy {
                this.launchPageConfig = when (launchPageConfig) {
                    LaunchPageConfig.Tools -> LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_TOOLS
                    LaunchPageConfig.Star -> LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_STAR
                }
            }
        }
    }

    suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig) {
        userPreferences.updateData {
            it.copy {
                this.themeBrandConfig = when (themeBrandConfig) {
                    ThemeBrandConfig.Default -> ThemeBrandConfigProto.THEME_BRAND_CONFIG_DEFAULT
                    ThemeBrandConfig.Android -> ThemeBrandConfigProto.THEME_BRAND_CONFIG_ANDROID
                }
            }
        }
    }

    suspend fun setThemeModeConfig(themeModeConfig: ThemeModeConfig) {
        userPreferences.updateData {
            it.copy {
                this.themeModeConfig = when (themeModeConfig) {
                    ThemeModeConfig.FollowSystem -> ThemeModeConfigProto.THEME_MODE_CONFIG_FOLLOW_SYSTEM
                    ThemeModeConfig.Light -> ThemeModeConfigProto.THEME_MODE_CONFIG_LIGHT
                    ThemeModeConfig.Dark -> ThemeModeConfigProto.THEME_MODE_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.useDynamicColor = useDynamicColor
            }
        }
    }

    suspend fun updateIsNotFirstLaunch() {
        userPreferences.updateData {
            it.copy {
                this.isNotFirstLaunch = true
            }
        }
    }
}
