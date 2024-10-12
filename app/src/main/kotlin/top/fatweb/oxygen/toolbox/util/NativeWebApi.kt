package top.fatweb.oxygen.toolbox.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.Base64
import android.webkit.JavascriptInterface
import androidx.activity.compose.ManagedActivityResultLauncher

class NativeWebApi(
    private val context: Context,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    @JavascriptInterface
    fun copyToClipboard(text: String): Boolean {
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        return clipboardManager?.let {
            it.setPrimaryClip(ClipData.newPlainText("copy", text))
            true
        } == true
    }

    @JavascriptInterface
    fun readClipboard(): String {
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        return clipboardManager?.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
    }

    @JavascriptInterface
    fun saveToDownloads(dataBase64: String, fileName: String): Boolean {
        val data = Base64.decode(dataBase64, Base64.DEFAULT)

        return saveToDownloads(
            context = context,
            permissionLauncher = permissionLauncher,
            data = data,
            fileName = fileName
        )
    }
}