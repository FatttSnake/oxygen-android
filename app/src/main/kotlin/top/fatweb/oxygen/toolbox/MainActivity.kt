package top.fatweb.oxygen.toolbox

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import top.fatweb.oxygen.toolbox.model.DarkThemeConfig
import top.fatweb.oxygen.toolbox.model.LanguageConfig
import top.fatweb.oxygen.toolbox.model.LaunchPageConfig
import top.fatweb.oxygen.toolbox.model.ThemeBrandConfig
import top.fatweb.oxygen.toolbox.monitor.NetworkMonitor
import top.fatweb.oxygen.toolbox.monitor.TimeZoneMonitor
import top.fatweb.oxygen.toolbox.repository.UserDataRepository
import top.fatweb.oxygen.toolbox.ui.OxygenApp
import top.fatweb.oxygen.toolbox.ui.rememberOxygenAppState
import top.fatweb.oxygen.toolbox.ui.theme.OxygenTheme
import top.fatweb.oxygen.toolbox.ui.util.LocalTimeZone
import top.fatweb.oxygen.toolbox.ui.util.LocaleUtils
import javax.inject.Inject

const val TAG = "MainActivity"

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
            }

            val darkTheme = shouldUseDarkTheme(uiState)
            LaunchedEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim, darkScrim
                    ) { darkTheme }
                )
            }

            val appState = rememberOxygenAppState(
                windowSizeClass = calculateWindowSizeClass(this),
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
                launchPageConfig = whatLaunchPage(uiState)
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalTimeZone provides currentTimeZone
            ) {
                OxygenTheme(
                    darkTheme = darkTheme,
                    androidTheme = shouldUseAndroidTheme(uiState),
                    dynamicColor = shouldUseDynamicColor(uiState)
                ) {
                    OxygenApp(appState)
                }
            }
            Log.d(TAG, "onCreate: C")
        }

        Log.d(TAG, "onCreate: D")
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UserDataRepositoryEntryPoint {
        val userDataRepository: UserDataRepository
    }

    override fun attachBaseContext(newBase: Context) {
        val userDataRepository =
            EntryPointAccessors.fromApplication<UserDataRepositoryEntryPoint>(newBase).userDataRepository
        super.attachBaseContext(LocaleUtils.attachBaseContext(newBase, runBlocking {
            userDataRepository.userData.first().languageConfig
        }))
    }
}

@Composable
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> isSystemInDarkTheme()
    is MainActivityUiState.Success -> when (uiState.userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}

@Composable
private fun shouldUseAndroidTheme(
    uiState: MainActivityUiState
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> false
    is MainActivityUiState.Success -> when (uiState.userData.themeBrandConfig) {
        ThemeBrandConfig.DEFAULT -> false
        ThemeBrandConfig.ANDROID -> true
    }
}

@Composable
private fun shouldUseDynamicColor(
    uiState: MainActivityUiState
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> true
    is MainActivityUiState.Success -> uiState.userData.useDynamicColor
}

@Composable
private fun whatLocale(
    uiState: MainActivityUiState
): LanguageConfig = when (uiState) {
    MainActivityUiState.Loading -> LanguageConfig.FOLLOW_SYSTEM
    is MainActivityUiState.Success -> uiState.userData.languageConfig
}

@Composable
private fun whatLaunchPage(
    uiState: MainActivityUiState
): LaunchPageConfig = when (uiState) {
    MainActivityUiState.Loading -> LaunchPageConfig.TOOLS
    is MainActivityUiState.Success -> uiState.userData.launchPageConfig
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
