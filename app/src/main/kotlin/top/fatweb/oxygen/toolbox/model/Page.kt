package top.fatweb.oxygen.toolbox.model

data class Page<T>(
    val total: Long,

    val pages: Long,

    val size: Long,

    val current: Long,

    val records: List<T>
)
