package com.hfad.forecastchangessaver.services

import android.content.Context
import android.util.Log
import com.hfad.forecastchangessaver.db.ForecastEntity
import com.hfad.forecastchangessaver.db.ForecastHistoryEntity
import com.hfad.forecastchangessaver.db.WeatherDataBase
import kotlinx.coroutines.flow.firstOrNull
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.LocalDate

const val WEATHER_PARSER_TAG = "WeatherParser"
const val UFA_URL = "https://www.gismeteo.ru/weather-ufa-4588/month/"

object WeatherParser {

    suspend fun parseAndSave(context: Context) {
        val currentDate = LocalDate.now()
        val dao = WeatherDataBase.getInstance(context).dateDao()
        val entity = dao.getByDate(currentDate).firstOrNull()
        val lastDate = entity?.forecastHistory?.last()?.forecastDate
        if (lastDate == null || lastDate.isBefore(currentDate)) {
            try {
                val doc = getHTML(UFA_URL)
                val newList = parseHTML(doc)
                val oldList = dao.getByDates(newList.map { it.date })
                val toUpdate = newList.zip(oldList).map { (new, old) ->
                    old.forecastHistory = old.forecastHistory + new.forecastHistory
                    old
                }
                dao.updateAll(toUpdate)
                dao.insertAll(newList.drop(oldList.size))
            } catch (ex: Exception) {
                Log.e(WEATHER_PARSER_TAG, "Error while parsing site")
                throw ex
            }
        }
    }

    private fun getHTML(url: String): Document =
        try {
            Jsoup.connect(url).get()
        } catch (ex: Exception) {
            Log.e(WEATHER_PARSER_TAG, "Error while getting html page")
            throw ex
        }

    private fun parseHTML(document: Document): List<ForecastHistoryEntity> {
        val rowItems =
            document.body().select("div.widget-month div.widget-body a:not(.item-past)")
        if (rowItems.size != 0) {
            val firstParsed = rowItems.first()?.selectFirst("div.date")?.text()
                ?.filter { it.isDigit() }
            if (firstParsed != null && firstParsed == LocalDate.now().dayOfMonth.toString()) {
                val currentDate = LocalDate.now()
                val forecastHistoryList = rowItems.mapIndexed { idx, row ->
                    parseRow(currentDate, currentDate.plusDays(idx.toLong()), row)
                }
                return forecastHistoryList
            } else
                Log.e(WEATHER_PARSER_TAG, "Weather first date and today don't equal")

        } else
            Log.e(WEATHER_PARSER_TAG, "Can't find elements with weather data")
        return listOf()
    }

    private fun parseRow(currentDate: LocalDate, onDate: LocalDate, row: Element): ForecastHistoryEntity {
        try {
            val iconId = row.select("div.icon use").attr("xlink:href")
                .removePrefix("#")
            val maxTemperature = row.select("div.temp .maxt .unit_temperature_c").text().toInt()
            val minTemperature = row.select("div.temp .mint .unit_temperature_c").text().toInt()
            return ForecastHistoryEntity(
                date = onDate, forecastHistory =
                listOf(ForecastEntity(currentDate, iconId, maxTemperature, minTemperature))
            )
        } catch (ex: Exception) {
            Log.e(WEATHER_PARSER_TAG, "Error while parsing row")
            throw ex
        }
    }
}