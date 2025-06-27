package top.fatweb.oxygen.toolbox.model.userdata

data class UserData(
    val languageConfig: LanguageConfig,

    val launchPageConfig: LaunchPageConfig,

    val themeBrandConfig: ThemeBrandConfig,

    val themeTypeConfig: ThemeTypeConfig,

    val useDynamicColor: Boolean,

    val isNotFirstLaunch: Boolean
)
