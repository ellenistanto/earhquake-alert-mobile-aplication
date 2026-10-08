package com.earthquakealert.app.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.earthquakealert.app.EarthquakeApplication
import com.earthquakealert.app.di.AppContainer
import com.earthquakealert.app.di.DefaultAppContainer
import java.io.IOException

class EarthquakeSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val appContainer: AppContainer = (applicationContext as? EarthquakeApplication)?.container
            ?: DefaultAppContainer(applicationContext)

        val earthquakeRepository = appContainer.earthquakeRepository
        val locationRepository = appContainer.locationRepository
        val settingsRepository = appContainer.settingsRepository
        val alertEvaluator = appContainer.alertEvaluator
        val notificationService = appContainer.notificationService

        return try {
            val settings = settingsRepository.getSettings()
            val userLocation = locationRepository.getLastKnownLocation()

            // Fetch latest earthquakes from BMKG endpoints and store in Room
            val latestEarthquakes = earthquakeRepository.getLatestEarthquakes()

            for (quake in latestEarthquakes) {
                // Prevent duplicate alerts
                if (earthquakeRepository.isEventProcessed(quake.id)) {
                    continue
                }

                val assessment = alertEvaluator.evaluate(
                    earthquake = quake,
                    userLocation = userLocation,
                    settings = settings
                )

                if (assessment.shouldAlert) {
                    notificationService.sendEarthquakeAlert(quake, assessment)
                    earthquakeRepository.markEventProcessed(quake.id, alertTriggered = true)
                } else {
                    earthquakeRepository.markEventProcessed(quake.id, alertTriggered = false)
                }
            }

            Result.success()
        } catch (e: IOException) {
            // Network failure: allow WorkManager backoff retry
            Result.retry()
        } catch (e: Exception) {
            // General failure: return retry or failure depending on attempt count
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val WORK_NAME = "earthquake_sync_worker"
    }
}
