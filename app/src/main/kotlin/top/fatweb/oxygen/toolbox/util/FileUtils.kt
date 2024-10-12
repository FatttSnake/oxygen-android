package top.fatweb.oxygen.toolbox.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.let

fun saveToDownloads(
    context: Context,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    data: ByteArray,
    fileName: String
) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    saveFileToDownloads(context = context, data = data, fileName = fileName)
} else {
    saveFileToDownloads(
        context = context,
        permissionLauncher = permissionLauncher,
        data = data,
        fileName = fileName
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun saveFileToDownloads(
    context: Context,
    data: ByteArray,
    fileName: String
): Boolean {
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
    } ?: run {
        Timber.e("Could not save file $fileName to Downloads")
        false
    }
}

private fun saveFileToDownloads(
    context: Context,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    data: ByteArray,
    fileName: String
): Boolean {
    if (!runBlocking {
            Permissions.requestWriteExternalStoragePermission(
                context,
                permissionLauncher
            )
        }) {
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
        Timber.e(e, "Could not save file $fileName to ${file.absolutePath}")
        false
    }
}