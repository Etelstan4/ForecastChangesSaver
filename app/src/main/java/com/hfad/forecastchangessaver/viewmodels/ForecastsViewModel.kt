package com.hfad.forecastchangessaver.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hfad.forecastchangessaver.db.WeatherDataBase
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneOffset

class ForecastsViewModel(application: Application, timeStamp: Long) :
    AndroidViewModel(application) {
    private val dao = WeatherDataBase.getInstance(application).dateDao()

    val data = dao.getByDate(Instant.ofEpochMilli(timeStamp).atZone(ZoneOffset.UTC).toLocalDate())
        .map { it?.forecastHistory }
}