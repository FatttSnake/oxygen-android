package top.fatweb.oxygen.toolbox.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

object Permissions {
    var continuation: Continuation<Boolean>? = null

    suspend fun requestWriteExternalStoragePermission(
        context: Context,
        permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        }

        return suspendCancellableCoroutine { continuation ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                continuation.resume(true)
            } else {
                this.continuation = continuation
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            continuation.invokeOnCancellation {
                continuation.resume(false)
            }
        }
    }
}
