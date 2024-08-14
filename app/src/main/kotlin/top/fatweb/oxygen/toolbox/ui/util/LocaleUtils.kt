package top.fatweb.oxygen.toolbox.ui.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import top.fatweb.oxygen.toolbox.model.userdata.LanguageConfig
import java.util.Locale

object LocaleUtils {
    fun switchLocale(activity: Activity, languageConfig: LanguageConfig) {
        val newLanguage = when (languageConfig) {
            LanguageConfig.FollowSystem -> ResourcesUtils.getSystemLocale().get(0)!!.language
            LanguageConfig.Chinese -> "zh"
            LanguageConfig.English -> "en"
        }
        val currentLanguage = ResourcesUtils.getAppLocale(activity).language
        if (newLanguage != currentLanguage) {
            activity.recreate()
        }
    }

    fun attachBaseContext(context: Context, languageConfig: LanguageConfig): Context {
        val locale: Locale = getLocaleFromLanguageConfig(languageConfig)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createConfigurationContext(context, locale)
        } else {
            updateConfiguration(context, locale)
        }
    }

    private fun getLocaleFromLanguageConfig(languageConfig: LanguageConfig): Locale =
        when (languageConfig) {
            LanguageConfig.FollowSystem -> ResourcesUtils.getSystemLocale().get(0)!!
            LanguageConfig.Chinese -> Locale("zh")
            LanguageConfig.English -> Locale("en")
        }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createConfigurationContext(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocales(LocaleList(locale))

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateConfiguration(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}