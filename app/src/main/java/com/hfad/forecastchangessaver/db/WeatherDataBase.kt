package com.hfad.forecastchangessaver.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hfad.forecastchangessaver.utils.Converters

@Database(entities = [ForecastHistoryEntity::class], version = 3, exportSchema = true)
@TypeConverters(Converters::class)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun dateDao(): MonthForecastDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDataBase? = null

        fun getInstance(context: Context): WeatherDataBase {
            synchronized(this) {
                var inst = INSTANCE
                if (inst == null) {
                    inst = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDataBase::class.java,
                        "weather_db"
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
                    INSTANCE = inst
                }
                return inst
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE date_table RENAME COLUMN  weather TO month_forecast;")
        database.execSQL("ALTER TABLE date_table RENAME TO month_forecast_table;")
    }
}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE month_forecast_table RENAME COLUMN  month_forecast TO forecast_history;")
        database.execSQL("ALTER TABLE month_forecast_table RENAME TO forecast_history_table;")
    }
}