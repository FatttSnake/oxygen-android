package top.fatweb.oxygen.toolbox.ui.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import java.util.Locale

object LocaleHelper {
    fun switchLocale(activity: Activity, languageConfig: LanguageConfig) {
        val newLanguage = getLocaleFromLanguageConfig(languageConfig).language
        val currentLanguage = ResourcesHelper.getAppLocale(activity).language
        if (newLanguage != currentLanguage) {
            activity.safeRecreate()
        }
    }

    fun attachBaseContext(context: Context, languageConfig: LanguageConfig): Context {
        val locale: Locale =
            getLocaleFromLanguageConfig(languageConfig)

        return createConfigurationContext(
            context,
            locale
        )
    }

    private fun getLocaleFromLanguageConfig(languageConfig: LanguageConfig): Locale =
        when (languageConfig) {
            LanguageConfig.FollowSystem -> ResourcesHelper.getSystemLocale().get(0)!!
            LanguageConfig.Chinese -> Locale.CHINESE
            LanguageConfig.English -> Locale.ENGLISH
        }

    private fun createConfigurationContext(context: Context, locale: Locale): Context {
        val configuration = Configuration(ResourcesHelper.getConfiguration(context))
        configuration.setLocales(LocaleList(locale))

        return context.createConfigurationContext(configuration)
    }
}

fun Activity.safeRecreate() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
    val intent = intent
    finish()
    startActivity(intent)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
