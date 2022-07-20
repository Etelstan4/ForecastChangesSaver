package com.hfad.forecastchangessaver.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hfad.forecastchangessaver.utils.Converters
import com.hfad.forecastchangessaver.utils.DateAsStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Entity(tableName = "forecast_history_table")
data class ForecastHistoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @TypeConverters(Converters::class)
    val date: LocalDate,     // Forecasts on date
    @ColumnInfo(name = "forecast_history")
    @TypeConverters(Converters::class)
    var forecastHistory: List<ForecastEntity>)

@Serializable
data class ForecastEntity (
    @Serializable(with = DateAsStringSerializer::class)
    @SerialName("date")
    val forecastDate: LocalDate,     // Forecast record date
    @SerialName("prec")
    val precipitation: String,  // value from attr xlink:href
    val tMax: Int,
    val tMin: Int
)