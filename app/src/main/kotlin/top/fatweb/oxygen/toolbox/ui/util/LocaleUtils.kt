package top.fatweb.oxygen.toolbox.ui.util

import android.app.Activity
import android.content.Context
import android.os.LocaleList
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

        return createConfigurationContext(context, locale)
    }

    private fun getLocaleFromLanguageConfig(languageConfig: LanguageConfig): Locale =
        when (languageConfig) {
            LanguageConfig.FollowSystem -> ResourcesUtils.getSystemLocale().get(0)!!
            LanguageConfig.Chinese -> Locale("zh")
            LanguageConfig.English -> Locale("en")
        }

    private fun createConfigurationContext(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocales(LocaleList(locale))

        return context.createConfigurationContext(configuration)
    }
}
