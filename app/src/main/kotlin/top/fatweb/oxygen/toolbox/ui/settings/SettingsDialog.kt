package top.fatweb.oxygen.toolbox.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.model.userdata.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.model.userdata.UserData
import top.fatweb.oxygen.toolbox.ui.component.ThemePreviews
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme
import top.fatweb.oxygen.toolbox.ui.theme.supportsDynamicTheming

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
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
        onchangeUseDynamicColor = viewModel::updateUseDynamicColor
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
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp),
                            text = stringResource(R.string.feature_settings_loading)
                        )
                    }

                    is SettingsUiState.Success -> {
                        SettingsPanel(
                            settings = settingsUiState.settings,
                            supportDynamicColor = supportDynamicColor,
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
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onDismiss() },
                text = stringResource(R.string.feature_settings_dismiss_dialog_button_text),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserData,
    supportDynamicColor: Boolean,
    onChangeLanguageConfig: (languageConfig: LanguageConfig) -> Unit,
    onChangeLaunchPageConfig: (launchPageConfig: LaunchPageConfig) -> Unit,
    onchangeThemeBrandConfig: (themeBrandConfig: ThemeBrandConfig) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onchangeUseDynamicColor: (useDynamicColor: Boolean) -> Unit
) {
    SettingsDialogSectionTitle(text = stringResource(R.string.feature_settings_language))
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_language_system_default),
            selected = settings.languageConfig == LanguageConfig.FOLLOW_SYSTEM,
            onClick = { onChangeLanguageConfig(LanguageConfig.FOLLOW_SYSTEM) }
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_language_chinese),
            selected = settings.languageConfig == LanguageConfig.CHINESE,
            onClick = { onChangeLanguageConfig(LanguageConfig.CHINESE) }
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_language_english),
            selected = settings.languageConfig == LanguageConfig.ENGLISH,
            onClick = { onChangeLanguageConfig(LanguageConfig.ENGLISH) }
        )
    }
    SettingsDialogSectionTitle(text = stringResource(R.string.feature_settings_launch_page))
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_launch_page_tools),
            selected = settings.launchPageConfig == LaunchPageConfig.TOOLS,
            onClick = { onChangeLaunchPageConfig(LaunchPageConfig.TOOLS) }
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_launch_page_star),
            selected = settings.launchPageConfig == LaunchPageConfig.STAR,
            onClick = { onChangeLaunchPageConfig(LaunchPageConfig.STAR) }
        )
    }
    SettingsDialogSectionTitle(text = stringResource(R.string.feature_settings_theme_brand))
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_theme_brand_default),
            selected = settings.themeBrandConfig == ThemeBrandConfig.DEFAULT,
            onClick = { onchangeThemeBrandConfig(ThemeBrandConfig.DEFAULT) }
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_theme_brand_android),
            selected = settings.themeBrandConfig == ThemeBrandConfig.ANDROID,
            onClick = { onchangeThemeBrandConfig(ThemeBrandConfig.ANDROID) }
        )
    }
    AnimatedVisibility(visible = settings.themeBrandConfig == ThemeBrandConfig.DEFAULT && supportDynamicColor) {
        Column(
            modifier = Modifier.selectableGroup()
        ) {
            SettingsDialogSectionTitle(text = stringResource(R.string.feature_settings_dynamic_color))
            SettingsDialogThemeChooserRow(
                text = stringResource(R.string.feature_settings_dynamic_color_enable),
                selected = settings.useDynamicColor,
                onClick = { onchangeUseDynamicColor(true) }
            )
            SettingsDialogThemeChooserRow(
                text = stringResource(R.string.feature_settings_dynamic_color_disable),
                selected = !settings.useDynamicColor,
                onClick = { onchangeUseDynamicColor(false) }
            )
        }
    }
    SettingsDialogSectionTitle(text = stringResource(R.string.feature_settings_dark_mode))
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_dark_mode_system_default),
            selected = settings.darkThemeConfig == DarkThemeConfig.FOLLOW_SYSTEM,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM) }
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_dark_mode_light),
            selected = settings.darkThemeConfig == DarkThemeConfig.LIGHT,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.LIGHT) }
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.feature_settings_dark_mode_dark),
            selected = settings.darkThemeConfig == DarkThemeConfig.DARK,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.DARK) }
        )
    }
}

@Composable
private fun SettingsDialogSectionTitle(text: String) {
    Text(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun SettingsDialogThemeChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@ThemePreviews
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
            onchangeUseDynamicColor = {}
        )
    }
}

@ThemePreviews
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
            onchangeUseDynamicColor = {}
        )
    }
}