package es.upsa.mimo.gamercollection.data.remote.model

import es.upsa.mimo.gamercollection.extensions.toDate
import es.upsa.mimo.gamercollection.extensions.toString
import java.util.Date
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DateSerializer : KSerializer<Date?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Date?) {
        val dateString = value.toString()
        dateString?.let { encoder.encodeString(it) } ?: encoder.encodeNull()
    }

    override fun deserialize(decoder: Decoder): Date? {
        val dateString = decoder.decodeString()
        return dateString.toDate("MMM dd, yyyy HH:mm:ss") ?: dateString.toDate()
    }
}