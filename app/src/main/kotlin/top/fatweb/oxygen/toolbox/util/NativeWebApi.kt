package top.fatweb.oxygen.toolbox.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.webkit.JavascriptInterface
import android.webkit.WebView

class NativeWebApi(
    private val context: Context,
    private val webView: WebView
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

    private fun callback(callback: String, vararg args: Any) {
        val jsCode =
            "$callback(${args.map { if (it is String) "'$it'" else it }.joinToString(", ")})"
        webView.evaluateJavascript(jsCode, null)
    }
}