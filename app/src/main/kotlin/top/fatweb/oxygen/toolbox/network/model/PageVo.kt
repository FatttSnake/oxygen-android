package top.fatweb.oxygen.toolbox.network.model

import kotlinx.serialization.Serializable
import top.fatweb.oxygen.toolbox.model.Page

@Serializable
data class PageVo<T>(
    val total: Long,

    val pages: Long,

    val size: Long,

    val current: Long,

    val records: List<T>
)

fun <T, R> PageVo<T>.asExternalModel(block: (T) -> R): Page<R> = Page(
    total = total,
    pages = pages,
    size = size,
    current = current,
    records = records.map(block)
)
