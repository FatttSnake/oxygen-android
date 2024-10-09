package top.fatweb.oxygen.toolbox.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NativeWebApi(
    private val context: Context,
    private val webView: WebView,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    @JavascriptInterface
    fun copyToClipboard(text: String): Boolean {
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        return clipboardManager?.let {
            it.setPrimaryClip(ClipData.newPlainText("copy", text))
            true
        } ?: false
    }

    @JavascriptInterface
    fun readClipboard(): String {
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        return clipboardManager?.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
    }

    @JavascriptInterface
    fun saveToDownloads(dataBase64: String, fileName: String): Boolean {
        val data = Base64.decode(dataBase64, Base64.DEFAULT)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveFileToDownloads(data = data, fileName = fileName)
        } else {
            saveFileToExternalDownloads(data = data, fileName = fileName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToDownloads(data: ByteArray, fileName: String): Boolean {
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val fileUri = resolver.insert(collection, contentValues)

        return fileUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(data)
                outputStream.flush()
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            true
        } ?: let {
            Timber.e("Could not save file $fileName to Downloads")
            false
        }
    }

    private fun saveFileToExternalDownloads(data: ByteArray, fileName: String): Boolean {
        if (!runBlocking { Permissions.requestWriteExternalStoragePermission(context, permissionLauncher) }) {
            return false
        }

        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        return try {
            FileOutputStream(file).apply {
                write(data)
                close()
            }
            true
        } catch (e: IOException) {
            Timber.e("Could not save file $fileName to ${file.absolutePath}", e)
            false
        }
    }

    private fun callback(callback: String, vararg args: Any) {
        val jsCode =
            "$callback(${args.map { if (it is String) "'$it'" else it }.joinToString(", ")})"
        webView.evaluateJavascript(jsCode, null)
    }
}