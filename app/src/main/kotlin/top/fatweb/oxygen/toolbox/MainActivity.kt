package top.fatweb.oxygen.toolbox

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.webkit.WebViewCompat
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import top.fatweb.oxygen.toolbox.model.userdata.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import top.fatweb.oxygen.toolbox.model.userdata.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.userdata.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.monitor.NetworkMonitor
import top.fatweb.oxygen.toolbox.monitor.TimeZoneMonitor
import top.fatweb.oxygen.toolbox.navigation.PREVIEW_ARG
import top.fatweb.oxygen.toolbox.navigation.navigateToToolView
import top.fatweb.oxygen.toolbox.repository.userdata.UserDataRepository
import top.fatweb.oxygen.toolbox.ui.OxygenApp
import top.fatweb.oxygen.toolbox.ui.rememberOxygenAppState
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme
import top.fatweb.oxygen.toolbox.ui.util.LocalTimeZone
import top.fatweb.oxygen.toolbox.ui.util.LocaleUtils
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                is MainActivityUiState.Success -> false
            }
        }
        enableEdgeToEdge()

        setContent {
            val locale = whatLocale(uiState)
            if (uiState != MainActivityUiState.Loading) {
                LaunchedEffect(locale) {
                    LocaleUtils.switchLocale(this@MainActivity, locale)
                }
                UseIsFirstLaunch(viewModel) {
                    CheckWebView(it)
                }
            }

            val darkTheme = shouldUseDarkTheme(uiState)
            LaunchedEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                )
            }

            val appState = rememberOxygenAppState(
                windowSizeClass = calculateWindowSizeClass(this),
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
                launchPageConfig = whatLaunchPage(uiState)
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalTimeZone provides currentTimeZone) {
                OxygenTheme(
                    darkTheme = darkTheme,
                    androidTheme = shouldUseAndroidTheme(uiState),
                    dynamicColor = shouldUseDynamicColor(uiState)
                ) {
                    OxygenApp(appState)
                }
            }

            LaunchedEffect(intent.data) {
                intent.data?.run {
                    val pathSegments = pathSegments
                    val preview = getBooleanQueryParameter(PREVIEW_ARG, false)
                    if (pathSegments.size == 2) {
                        appState.navController.navigateToToolView(
                            username = pathSegments[0],
                            toolId = pathSegments[1],
                            preview = preview
                        )
                    }
                }
            }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UserDataRepositoryEntryPoint {
        val userDataRepository: UserDataRepository
    }

    override fun attachBaseContext(newBase: Context) {
        val userDataRepository =
            EntryPointAccessors.fromApplication<UserDataRepositoryEntryPoint>(newBase).userDataRepository
        super.attachBaseContext(
            LocaleUtils.attachBaseContext(
                context = newBase,
                languageConfig = runBlocking {
                    userDataRepository.userData.first().languageConfig
                }
            )
        )
    }
}

@Composable
private fun UseIsFirstLaunch(viewModel: MainActivityViewModel, callback: @Composable (ondDismiss: () -> Unit) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (!whatIsFirstLaunch(uiState)) {
        return
    }

    callback {
        viewModel.updateIsNotFirstLaunch()
    }
}

@Composable
private fun whatIsFirstLaunch(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> false
        is MainActivityUiState.Success ->
            !uiState.userData.isNotFirstLaunch
    }

@Composable
private fun CheckWebView(onDismiss: () -> Unit) {
    val versionName = WebViewCompat.getCurrentWebViewPackage(LocalContext.current)?.versionName
    if (versionName == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {Text(text = stringResource(R.string.core_web_view_warning))},
            text = { Text(text = stringResource(R.string.core_cannot_load_web_view_version)) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.core_dismiss))
                }
            }
        )
        return
    }
    if (versionName.split(".").first().toInt() < 80) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {Text(text = stringResource(R.string.core_web_view_warning))},
            text = { Text(text = stringResource(R.string.core_web_view_version_too_low)) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.core_dismiss))
                }
            }
        )
        return
    }
}

@Composable
private fun shouldUseDarkTheme(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> isSystemInDarkTheme()
        is MainActivityUiState.Success ->
            when (uiState.userData.darkThemeConfig) {
                DarkThemeConfig.FollowSystem -> isSystemInDarkTheme()
                DarkThemeConfig.Light -> false
                DarkThemeConfig.Dark -> true
            }
    }

@Composable
private fun shouldUseAndroidTheme(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> false
        is MainActivityUiState.Success ->
            when (uiState.userData.themeBrandConfig) {
                ThemeBrandConfig.Default -> false
                ThemeBrandConfig.Android -> true
            }
    }

@Composable
private fun shouldUseDynamicColor(uiState: MainActivityUiState): Boolean =
    when (uiState) {
        MainActivityUiState.Loading -> true
        is MainActivityUiState.Success -> uiState.userData.useDynamicColor
    }

@Composable
private fun whatLocale(uiState: MainActivityUiState): LanguageConfig =
    when (uiState) {
        MainActivityUiState.Loading -> LanguageConfig.FollowSystem
        is MainActivityUiState.Success -> uiState.userData.languageConfig
    }

@Composable
private fun whatLaunchPage(uiState: MainActivityUiState): LaunchPageConfig =
    when (uiState) {
        MainActivityUiState.Loading -> LaunchPageConfig.Tools
        is MainActivityUiState.Success -> uiState.userData.launchPageConfig
    }
