package com.hfad.forecastchangessaver.db

import androidx.room.*
import com.hfad.forecastchangessaver.utils.Converters
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MonthForecastDao {

    @Insert
    fun insert(forecastHistoryEntity: ForecastHistoryEntity)

    @Insert
    fun insertAll(entityList: List<ForecastHistoryEntity>)

    @Update
    fun update(forecastHistoryEntity: ForecastHistoryEntity)

    @Update
    fun updateAll(entityList: List<ForecastHistoryEntity>)

    @Delete
    fun delete(forecastHistoryEntity: ForecastHistoryEntity)

    @Query("SELECT * FROM forecast_history_table WHERE id = :id")
    fun getById(id: Long): ForecastHistoryEntity

    @Query("SELECT * FROM forecast_history_table WHERE date = :date")
    @TypeConverters(Converters::class)
    fun getByDate(date: LocalDate): Flow<ForecastHistoryEntity?>

    @Query("SELECT * FROM forecast_history_table WHERE date IN (:dates)")
    @TypeConverters(Converters::class)
    fun getByDates(dates: List<LocalDate>): List<ForecastHistoryEntity>

    @Query("SELECT date FROM forecast_history_table ORDER BY date DESC LIMIT 1")
    @TypeConverters(Converters::class)
    fun getLastDate(): LocalDate?

    @Query("SELECT date FROM forecast_history_table ORDER BY date LIMIT 1")
    @TypeConverters(Converters::class)
    fun getFirstDate(): LocalDate?

    @Query("SELECT * FROM forecast_history_table")
    fun getAll(): List<ForecastHistoryEntity>
}