package com.hfad.forecastchangessaver

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hfad.forecastchangessaver.db.MIGRATION_1_2
import com.hfad.forecastchangessaver.db.MIGRATION_2_3
import com.hfad.forecastchangessaver.db.WeatherDataBase
import com.hfad.forecastchangessaver.utils.Converters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

const val MIGRATION_TEST_TAG = "MigrationTest"

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration_test"

    private val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3)

    private val testDate = 1653264000000L
    private val testDateWeather =
        """[{"date":"23.05.2022","prec":"c4","tMax":12,"tMin":5},{"date":"24.05.2022","prec":"c4_r2","tMax":14,"tMin":6},{"date":"25.05.2022","prec":"c4_r2","tMax":11,"tMin":8},{"date":"26.05.2022","prec":"d_c3_r1","tMax":11,"tMin":7}]"""

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(), WeatherDataBase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun allMigrations() = runBlocking {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                "INSERT INTO date_table (date, weather) VALUES ($testDate, '$testDateWeather')"
            )
            close()
        }

        val migratedDb = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            WeatherDataBase::class.java, TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build()

        val dbWeatherEntity =
            migratedDb.dateDao().getByDate(Instant.ofEpochMilli(testDate).atZone(ZoneOffset.UTC).toLocalDate())
        migratedDb.close()

        assertEquals(
            Converters
                .fromWeatherForecastList(
                    dbWeatherEntity.firstOrNull()?.forecastHistory ?: listOf()
                ),
            testDateWeather
        )
    }

}