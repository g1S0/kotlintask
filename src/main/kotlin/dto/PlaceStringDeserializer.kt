package org.example.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

object PlaceStringDeserializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Place", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String? {
        val jsonElement = (decoder as JsonDecoder).decodeJsonElement()

        return when (jsonElement) {
            is JsonObject -> jsonElement["id"]?.toString()
            else -> null
        }
    }

    override fun serialize(encoder: Encoder, value: String?) {
        val jsonElement = value?.let {
            buildJsonObject {
                put("id", JsonPrimitive(it))
            }
        } ?: JsonPrimitive(null as String?)

        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }
}