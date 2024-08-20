package top.fatweb.oxygen.toolbox.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.IntDef
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HttpLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Timber.i(message = message)
    }
}

class OxygenLogTree(
    context: Context
) : Timber.DebugTree() {
    @IntDef(Log.ASSERT, Log.ERROR, Log.WARN, Log.INFO, Log.DEBUG, Log.VERBOSE)
    @Retention(
        AnnotationRetention.SOURCE
    )
    annotation class Level

    private val fileNamePrefix = "oxygen_toolbox"
    private val fileNamePostfix = ".log"
    private val maxFileSize: Long = 10 * 1024 * 1024
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val logDir = File(context.cacheDir, "logs")
    private val logFile = File(logDir, "$fileNamePrefix$fileNamePostfix")

    @SuppressLint("LogNotTimber")
    override fun log(@Level priority: Int, tag: String?, message: String, t: Throwable?) {
        checkFile()
        try {
            if (logFile.length() >= maxFileSize) {
                rotateLogFile()
            }
            logFile.appendText(text = format(priority = priority, tag = tag, message = message, t = t))
        } catch (e: Exception) {
            Log.e("OxygenLogTree", "Error writing log message to file", e)
        }
    }

    private fun checkFile() {
        try {
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            logFile.createNewFile()
        } catch (_: Exception) {}
    }

    private fun format(@Level priority: Int, tag: String?, message: String, t: Throwable?) = "${
        LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    }\t$tag\t${
        when (priority) {
            Log.ASSERT -> "Assert: "
            Log.DEBUG -> "Debug: "
            Log.ERROR -> "Error: "
            Log.INFO -> "Info: "
            Log.VERBOSE -> "Verbose: "
            Log.WARN -> "Warn: "
            else -> "Unknown: "
        }
    } $message${t?.run { " ${toString()}" } ?: ""}\n"

    private fun rotateLogFile() {
        logFile.renameTo(
            File(
                logFile.parent,
                "$fileNamePrefix${LocalDateTime.now().format(dateFormat)}$fileNamePostfix"
            )
        )
        logFile.createNewFile()
    }
}
