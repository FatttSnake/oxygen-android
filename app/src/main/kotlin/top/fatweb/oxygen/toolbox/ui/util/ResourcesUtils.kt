package top.fatweb.oxygen.toolbox.ui.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

object ResourcesUtils {
    fun getConfiguration(context: Context) = context.resources.configuration

    fun getDisplayMetrics(context: Context) = context.resources.displayMetrics

    @Suppress("DEPRECATION")
    fun getAppLocale(context: Context): Locale =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) getConfiguration(context).locales.get(0)
        else getConfiguration(context).locale

    fun getSystemLocale(): LocaleListCompat =
        ConfigurationCompat.getLocales(Resources.getSystem().configuration)

    fun getAppVersionName(context: Context): String =
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)?.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }

    @Suppress("DEPRECATION")
    fun getAppVersionCode(context: Context): Long =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                context.packageManager.getPackageInfo(context.packageName, 0)?.longVersionCode ?: -1
            else context.packageManager.getPackageInfo(
                context.packageName,
                0
            )?.versionCode?.toLong() ?: -1
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }

    fun getString(context: Context, @StringRes resId: Int): String =
        context.resources.getString(resId)
}
