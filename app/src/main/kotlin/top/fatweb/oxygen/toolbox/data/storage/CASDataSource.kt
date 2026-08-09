package top.fatweb.oxygen.toolbox.data.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import top.fatweb.oxygen.toolbox.util.readFile
import top.fatweb.oxygen.toolbox.util.saveToFile
import top.fatweb.oxygen.toolbox.util.sha256HexString
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.fileSize
import kotlin.io.path.isRegularFile

class CASDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private fun String.splitFileName() =
        Pair(
            this.substring(0, 2),
            this.substring(2)
        )

    private fun String.resolvePath(): Path {
        val (dir, fileName) = this.splitFileName()

        return Path(context.filesDir.path, dir, fileName)
    }

    fun save(content: ByteArray): String {
        val key = content.sha256HexString()
        if (this.exists(key)) {
            return key
        }

        val (dir, fileName) = key.splitFileName()

        return content
            .saveToFile(
                base = context.filesDir.path,
                dir,
                fileName,
                compressStreamFactory = ::GZIPOutputStream
            )
            .let { key }
    }

    fun load(key: String): ByteArray? {
        if (!this.exists(key)) {
            return null
        }

        return key.resolvePath().readFile(::GZIPInputStream)
    }

    fun exists(key: String): Boolean =
        key.resolvePath().isRegularFile()

    fun delete(key: String) =
        runCatching {
            key.resolvePath().deleteIfExists()
        }.getOrDefault(false)

    fun size(key: String): Long? {
        if (!this.exists(key)) {
            return null
        }

        return runCatching {
            key.resolvePath().fileSize()
        }.getOrNull()
    }
}
