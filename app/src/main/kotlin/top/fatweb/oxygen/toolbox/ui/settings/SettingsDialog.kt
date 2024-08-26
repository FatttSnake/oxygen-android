package top.fatweb.oxygen.toolbox.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.OxygenIcons
import top.fatweb.oxygen.toolbox.model.userdata.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.userdata.UserData
import top.fatweb.oxygen.toolbox.ui.component.DialogChooserRow
import top.fatweb.oxygen.toolbox.ui.component.DialogClickerRow
import top.fatweb.oxygen.toolbox.ui.component.DialogSectionGroup
import top.fatweb.oxygen.toolbox.ui.component.DialogSectionTitle
import top.fatweb.oxygen.toolbox.ui.component.Indicator
import top.fatweb.oxygen.toolbox.ui.theme.OxygenPreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme
import top.fatweb.oxygen.toolbox.ui.theme.supportsDynamicTheming

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onDismiss: () -> Unit
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    SettingsDialog(
        modifier = modifier,
        settingsUiState = settingsUiState,
        onNavigateToLibraries = onNavigateToLibraries,
        onNavigateToAbout = onNavigateToAbout,
        onDismiss = onDismiss,
        onChangeLanguageConfig = viewModel::updateLanguageConfig,
        onChangeLaunchPageConfig = viewModel::updateLaunchPageConfig,
        onchangeThemeBrandConfig = viewModel::updateThemeBrandConfig,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
        onchangeUseDynamicColor = viewModel::updateUseDynamicColor
    )
}

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    settingsUiState: SettingsUiState,
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onDismiss: () -> Unit,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onChangeLanguageConfig: (languageConfig: LanguageConfig) -> Unit,
    onChangeLaunchPageConfig: (launchPageConfig: LaunchPageConfig) -> Unit,
    onchangeThemeBrandConfig: (themeBrandConfig: ThemeBrandConfig) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onchangeUseDynamicColor: (useDynamicColor: Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        modifier = modifier
            .widthIn(max = configuration.screenWidthDp.dp - 80.dp)
            .heightIn(max = configuration.screenHeightDp.dp - 40.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.feature_settings_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            HorizontalDivider()
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                when (settingsUiState) {
                    SettingsUiState.Loading -> {
                        Indicator()
                    }

                    is SettingsUiState.Success -> {
                        SettingsPanel(
                            settings = settingsUiState.settings,
                            supportDynamicColor = supportDynamicColor,
                            onNavigateToLibraries = onNavigateToLibraries,
                            onNavigateToAbout = onNavigateToAbout,
                            onDismiss = onDismiss,
                            onChangeLanguageConfig = onChangeLanguageConfig,
                            onChangeLaunchPageConfig = onChangeLaunchPageConfig,
                            onchangeThemeBrandConfig = onchangeThemeBrandConfig,
                            onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                            onchangeUseDynamicColor = onchangeUseDynamicColor
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(R.string.core_ok),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserData,
    supportDynamicColor: Boolean,
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onDismiss: () -> Unit,
    onChangeLanguageConfig: (languageConfig: LanguageConfig) -> Unit,
    onChangeLaunchPageConfig: (launchPageConfig: LaunchPageConfig) -> Unit,
    onchangeThemeBrandConfig: (themeBrandConfig: ThemeBrandConfig) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onchangeUseDynamicColor: (useDynamicColor: Boolean) -> Unit
) {
    DialogSectionTitle(text = stringResource(R.string.feature_settings_language))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_system_default),
            selected = settings.languageConfig == LanguageConfig.FollowSystem,
            onClick = { onChangeLanguageConfig(LanguageConfig.FollowSystem) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_chinese),
            selected = settings.languageConfig == LanguageConfig.Chinese,
            onClick = { onChangeLanguageConfig(LanguageConfig.Chinese) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_english),
            selected = settings.languageConfig == LanguageConfig.English,
            onClick = { onChangeLanguageConfig(LanguageConfig.English) }
        )
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_launch_page))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_launch_page_tools),
            selected = settings.launchPageConfig == LaunchPageConfig.Tools,
            onClick = { onChangeLaunchPageConfig(LaunchPageConfig.Tools) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_launch_page_star),
            selected = settings.launchPageConfig == LaunchPageConfig.Star,
            onClick = { onChangeLaunchPageConfig(LaunchPageConfig.Star) }
        )
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_theme_brand))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_brand_default),
            selected = settings.themeBrandConfig == ThemeBrandConfig.Default,
            onClick = { onchangeThemeBrandConfig(ThemeBrandConfig.Default) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_brand_android),
            selected = settings.themeBrandConfig == ThemeBrandConfig.Android,
            onClick = { onchangeThemeBrandConfig(ThemeBrandConfig.Android) }
        )
    }
    AnimatedVisibility(visible = settings.themeBrandConfig == ThemeBrandConfig.Default && supportDynamicColor) {
        DialogSectionGroup {
            DialogSectionTitle(text = stringResource(R.string.feature_settings_dynamic_color))
            DialogChooserRow(
                text = stringResource(R.string.feature_settings_dynamic_color_enable),
                selected = settings.useDynamicColor,
                onClick = { onchangeUseDynamicColor(true) }
            )
            DialogChooserRow(
                text = stringResource(R.string.feature_settings_dynamic_color_disable),
                selected = !settings.useDynamicColor,
                onClick = { onchangeUseDynamicColor(false) }
            )
        }
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_dark_mode))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_dark_mode_system_default),
            selected = settings.darkThemeConfig == DarkThemeConfig.FollowSystem,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.FollowSystem) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_dark_mode_light),
            selected = settings.darkThemeConfig == DarkThemeConfig.Light,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.Light) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_dark_mode_dark),
            selected = settings.darkThemeConfig == DarkThemeConfig.Dark,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.Dark) }
        )
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_more))
    DialogSectionGroup {
        DialogClickerRow(
            icon = OxygenIcons.Code,
            text = stringResource(R.string.feature_settings_open_source_license),
            onClick = {
                onNavigateToLibraries()
                onDismiss()
            }
        )
        DialogClickerRow(
            icon = OxygenIcons.Info,
            text = stringResource(R.string.feature_settings_more_about),
            onClick = {
                onNavigateToAbout()
                onDismiss()
            }
        )
    }
}


@OxygenPreviews
@Composable
private fun SettingsDialogLoadingPreview() {
    OxygenTheme {
        SettingsDialog(
            onNavigateToLibraries = {},
            onNavigateToAbout = {},
            onDismiss = { },
            settingsUiState = SettingsUiState.Loading,
            onChangeLanguageConfig = {},
            onChangeLaunchPageConfig = {},
            onchangeThemeBrandConfig = {},
            onChangeDarkThemeConfig = {},
            onchangeUseDynamicColor = {}
        )
    }
}

@OxygenPreviews
@Composable
private fun SettingDialogPreview() {
    OxygenTheme {
        SettingsDialog(
            onNavigateToLibraries = {},
            onNavigateToAbout = {},
            onDismiss = {},
            settingsUiState = SettingsUiState.Success(
                UserData(
                    languageConfig = LanguageConfig.FollowSystem,
                    launchPageConfig = LaunchPageConfig.Tools,
                    themeBrandConfig = ThemeBrandConfig.Default,
                    darkThemeConfig = DarkThemeConfig.FollowSystem,
                    useDynamicColor = true
                )
            ),
            onChangeLanguageConfig = {},
            onChangeLaunchPageConfig = {},
            onchangeThemeBrandConfig = {},
            onChangeDarkThemeConfig = {},
            onchangeUseDynamicColor = {}
        )
    }
}
