package top.fatweb.oxygen.toolbox.repository.storage

interface CASRepository {
    fun save(content: ByteArray): String

    fun save(content: String): String

    fun load(key: String): ByteArray?

    fun exists(key: String): Boolean

    fun delete(key: String): Boolean

    fun size(key: String): Long?
}
