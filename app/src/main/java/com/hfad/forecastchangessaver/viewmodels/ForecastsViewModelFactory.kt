package com.hfad.forecastchangessaver.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ForecastsViewModelFactory(private val application: Application, private val timeStamp: Long) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForecastsViewModel::class.java))
            return ForecastsViewModel(application, timeStamp) as T
        throw IllegalArgumentException("Unknown ViewModel")
    }

}