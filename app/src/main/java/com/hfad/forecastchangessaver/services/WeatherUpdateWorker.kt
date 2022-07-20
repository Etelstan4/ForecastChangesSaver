package com.hfad.forecastchangessaver.services

import android.content.Context
import android.util.Log
import androidx.work.*
import com.hfad.forecastchangessaver.services.WeatherParser.parseAndSave
import java.util.concurrent.TimeUnit

class WeatherUpdateWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            parseAndSave(appContext)
            Result.success()
        } catch (ex: Exception) {
            Log.e(WORKER_TAG, "Weather update worker error: ${ex.message}")
            Result.failure()
        }
    }


    companion object {
        const val WORKER_TAG = "Weather_update_worker"

        fun addUpdateWorker(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest =
                PeriodicWorkRequest
                    .Builder(WeatherUpdateWorker::class.java, 6, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

    }
}