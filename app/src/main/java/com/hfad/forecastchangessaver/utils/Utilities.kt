package com.hfad.forecastchangessaver.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object Utilities {

    fun LocalDate?.toShortString(): String? =
        this?.let { DateTimeFormatter.ofPattern("dd MMM").format(it) }

    fun LocalDate?.toMillisUTC(): Long? =
        this?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
}

object DateAsStringSerializer : KSerializer<LocalDate> {
    private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) =
        encoder.encodeString(dateFormat.format(value))

    override fun deserialize(decoder: Decoder): LocalDate =
        LocalDate.parse(decoder.decodeString(), dateFormat) ?: LocalDate.MIN

}