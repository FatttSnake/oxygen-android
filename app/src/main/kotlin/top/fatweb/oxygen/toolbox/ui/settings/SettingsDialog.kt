package top.fatweb.oxygen.toolbox.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.icon.Loading
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
import top.fatweb.oxygen.toolbox.ui.theme.OxygenPreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme
import top.fatweb.oxygen.toolbox.ui.theme.supportsDynamicTheming

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    SettingsDialog(
        modifier = modifier,
        settingsUiState = settingsUiState,
        onDismiss = onDismiss,
        onChangeLanguageConfig = viewModel::updateLanguageConfig,
        onChangeLaunchPageConfig = viewModel::updateLaunchPageConfig,
        onchangeThemeBrandConfig = viewModel::updateThemeBrandConfig,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
        onchangeUseDynamicColor = viewModel::updateUseDynamicColor,
        onNavigateToLibraries = onNavigateToLibraries,
        onNavigateToAbout = onNavigateToAbout
    )
}

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    settingsUiState: SettingsUiState,
    onDismiss: () -> Unit,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onChangeLanguageConfig: (languageConfig: LanguageConfig) -> Unit,
    onChangeLaunchPageConfig: (launchPageConfig: LaunchPageConfig) -> Unit,
    onchangeThemeBrandConfig: (themeBrandConfig: ThemeBrandConfig) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onchangeUseDynamicColor: (useDynamicColor: Boolean) -> Unit,
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

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
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val angle by infiniteTransition.animateFloat(
                                initialValue = 0F,
                                targetValue = 360F,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800, easing = Ease),
                                ), label = "angle"
                            )
                            Icon(
                                modifier = Modifier
                                    .size(32.dp)
                                    .graphicsLayer { rotationZ = angle },
                                imageVector = OxygenIcons.Loading,
                                contentDescription = ""
                            )
                        }
                    }

                    is SettingsUiState.Success -> {
                        SettingsPanel(
                            settings = settingsUiState.settings,
                            supportDynamicColor = supportDynamicColor,
                            onDismiss = onDismiss,
                            onChangeLanguageConfig = onChangeLanguageConfig,
                            onChangeLaunchPageConfig = onChangeLaunchPageConfig,
                            onchangeThemeBrandConfig = onchangeThemeBrandConfig,
                            onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                            onchangeUseDynamicColor = onchangeUseDynamicColor,
                            onNavigateToLibraries = onNavigateToLibraries,
                            onNavigateToAbout = onNavigateToAbout
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
    onDismiss: () -> Unit,
    onChangeLanguageConfig: (languageConfig: LanguageConfig) -> Unit,
    onChangeLaunchPageConfig: (launchPageConfig: LaunchPageConfig) -> Unit,
    onchangeThemeBrandConfig: (themeBrandConfig: ThemeBrandConfig) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onchangeUseDynamicColor: (useDynamicColor: Boolean) -> Unit,
    onNavigateToLibraries: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    DialogSectionTitle(text = stringResource(R.string.feature_settings_language))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_system_default),
            selected = settings.languageConfig == LanguageConfig.FOLLOW_SYSTEM,
            onClick = { onChangeLanguageConfig(LanguageConfig.FOLLOW_SYSTEM) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_chinese),
            selected = settings.languageConfig == LanguageConfig.CHINESE,
            onClick = { onChangeLanguageConfig(LanguageConfig.CHINESE) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_language_english),
            selected = settings.languageConfig == LanguageConfig.ENGLISH,
            onClick = { onChangeLanguageConfig(LanguageConfig.ENGLISH) }
        )
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_launch_page))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_launch_page_tools),
            selected = settings.launchPageConfig == LaunchPageConfig.TOOLS,
            onClick = { onChangeLaunchPageConfig(LaunchPageConfig.TOOLS) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_launch_page_star),
            selected = settings.launchPageConfig == LaunchPageConfig.STAR,
            onClick = { onChangeLaunchPageConfig(LaunchPageConfig.STAR) }
        )
    }
    DialogSectionTitle(text = stringResource(R.string.feature_settings_theme_brand))
    DialogSectionGroup {
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_brand_default),
            selected = settings.themeBrandConfig == ThemeBrandConfig.DEFAULT,
            onClick = { onchangeThemeBrandConfig(ThemeBrandConfig.DEFAULT) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_theme_brand_android),
            selected = settings.themeBrandConfig == ThemeBrandConfig.ANDROID,
            onClick = { onchangeThemeBrandConfig(ThemeBrandConfig.ANDROID) }
        )
    }
    AnimatedVisibility(visible = settings.themeBrandConfig == ThemeBrandConfig.DEFAULT && supportDynamicColor) {
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
            selected = settings.darkThemeConfig == DarkThemeConfig.FOLLOW_SYSTEM,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_dark_mode_light),
            selected = settings.darkThemeConfig == DarkThemeConfig.LIGHT,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.LIGHT) }
        )
        DialogChooserRow(
            text = stringResource(R.string.feature_settings_dark_mode_dark),
            selected = settings.darkThemeConfig == DarkThemeConfig.DARK,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.DARK) }
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
            onDismiss = { },
            settingsUiState = SettingsUiState.Loading,
            onChangeLanguageConfig = {},
            onChangeLaunchPageConfig = {},
            onchangeThemeBrandConfig = {},
            onChangeDarkThemeConfig = {},
            onchangeUseDynamicColor = {},
            onNavigateToLibraries = {},
            onNavigateToAbout = {}
        )
    }
}

@OxygenPreviews
@Composable
private fun SettingDialogPreview() {
    OxygenTheme {
        SettingsDialog(
            onDismiss = {},
            settingsUiState = SettingsUiState.Success(
                UserData(
                    languageConfig = LanguageConfig.FOLLOW_SYSTEM,
                    launchPageConfig = LaunchPageConfig.TOOLS,
                    themeBrandConfig = ThemeBrandConfig.DEFAULT,
                    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                    useDynamicColor = true
                )
            ),
            onChangeLanguageConfig = {},
            onChangeLaunchPageConfig = {},
            onchangeThemeBrandConfig = {},
            onChangeDarkThemeConfig = {},
            onchangeUseDynamicColor = {},
            onNavigateToLibraries = {},
            onNavigateToAbout = {}
        )
    }
}