package com.hfad.forecastchangessaver.utils

import androidx.room.TypeConverter
import com.hfad.forecastchangessaver.db.ForecastEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object Converters {

    @TypeConverter
    fun fromWeatherForecastList(weatherForecastList: List<ForecastEntity>): String {
        return Json.encodeToString(weatherForecastList)
    }

    @TypeConverter
    fun toWeatherForecastList(weatherForecastListString: String): List<ForecastEntity> {
        return Json.decodeFromString(weatherForecastListString)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? =
        date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun timestampToDate(millis: Long?): LocalDate? = millis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
    }
}