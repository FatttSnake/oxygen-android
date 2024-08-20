package top.fatweb.oxygen.toolbox.network.serializer

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName = "LocalDateTime", kind = PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime =
        LocalDateTime.parse(input = decoder.decodeString().removeSuffix("Z"))

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString().padEnd(length = 24, padChar = 'Z'))
    }
}
