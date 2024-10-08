package top.fatweb.oxygen.toolbox.ui.util

import androidx.compose.runtime.compositionLocalOf
import kotlinx.datetime.TimeZone

val LocalTimeZone = compositionLocalOf { TimeZone.currentSystemDefault() }