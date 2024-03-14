package top.fatweb.oxygen.toolbox.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import top.fatweb.oxygen.toolbox.model.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.LanguageConfig
import top.fatweb.oxygen.toolbox.model.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.UserData
import javax.inject.Inject

class OxygenPreferencesDataSource @Inject constructor(
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
                    -> LanguageConfig.FOLLOW_SYSTEM

                    LanguageConfigProto.LANGUAGE_CONFIG_CHINESE
                    -> LanguageConfig.CHINESE

                    LanguageConfigProto.LANGUAGE_CONFIG_ENGLISH
                    -> LanguageConfig.ENGLISH
                },
                launchPageConfig = when (it.launchPageConfig) {
                    null,
                    LaunchPageConfigProto.UNRECOGNIZED,
                    LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_UNSPECIFIED,
                    LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_TOOLS
                    -> LaunchPageConfig.TOOLS

                    LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_STAR
                    -> LaunchPageConfig.STAR
                },
                themeBrandConfig = when (it.themeBrandConfig) {
                    null,
                    ThemeBrandConfigProto.UNRECOGNIZED,
                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_UNSPECIFIED,
                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_DEFAULT
                    ->
                        ThemeBrandConfig.DEFAULT

                    ThemeBrandConfigProto.THEME_BRAND_CONFIG_ANDROID
                    -> ThemeBrandConfig.ANDROID
                },
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    ->
                        DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    -> DarkThemeConfig.LIGHT

                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                    -> DarkThemeConfig.DARK
                },
                useDynamicColor = it.useDynamicColor
            )
        }

    suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        userPreferences.updateData {
            it.copy {
                this.languageConfig = when (languageConfig) {
                    LanguageConfig.FOLLOW_SYSTEM -> LanguageConfigProto.LANGUAGE_CONFIG_FOLLOW_SYSTEM
                    LanguageConfig.CHINESE -> LanguageConfigProto.LANGUAGE_CONFIG_CHINESE
                    LanguageConfig.ENGLISH -> LanguageConfigProto.LANGUAGE_CONFIG_ENGLISH
                }
            }
        }
    }

    suspend fun setLaunchPageConfig(launchPageConfig: LaunchPageConfig) {
        userPreferences.updateData {
            it.copy {
                this.launchPageConfig = when (launchPageConfig) {
                    LaunchPageConfig.TOOLS -> LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_TOOLS
                    LaunchPageConfig.STAR -> LaunchPageConfigProto.LAUNCH_PAGE_CONFIG_STAR
                }
            }
        }
    }

    suspend fun setThemeBrandConfig(themeBrandConfig: ThemeBrandConfig) {
        userPreferences.updateData {
            it.copy {
                this.themeBrandConfig = when (themeBrandConfig) {
                    ThemeBrandConfig.DEFAULT -> ThemeBrandConfigProto.THEME_BRAND_CONFIG_DEFAULT
                    ThemeBrandConfig.ANDROID -> ThemeBrandConfigProto.THEME_BRAND_CONFIG_ANDROID
                }
            }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
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
}
