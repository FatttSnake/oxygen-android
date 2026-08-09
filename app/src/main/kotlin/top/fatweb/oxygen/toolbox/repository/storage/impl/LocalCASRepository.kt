package top.fatweb.oxygen.toolbox.repository.storage.impl

import top.fatweb.oxygen.toolbox.data.storage.CASDataSource
import top.fatweb.oxygen.toolbox.repository.storage.CASRepository
import javax.inject.Inject

class LocalCASRepository @Inject constructor(
    private val casDataSource: CASDataSource
) : CASRepository {
    override fun save(content: ByteArray): String =
        casDataSource.save(content)

    override fun save(content: String): String =
        casDataSource.save(content.toByteArray())

    override fun load(key: String): ByteArray? =
        casDataSource.load(key)

    override fun exists(key: String): Boolean =
        casDataSource.exists(key)

    override fun delete(key: String): Boolean =
        casDataSource.delete(key)

    override fun size(key: String): Long? =
        casDataSource.size(key)
}