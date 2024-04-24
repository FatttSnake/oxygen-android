package top.fatweb.oxygen.toolbox.model.lib

import kotlinx.serialization.Serializable

@Serializable
data class Dependencies(
    val metadata: Metadata,

    val libraries: List<Library>,

    val licenses: Map<String, License>
)
