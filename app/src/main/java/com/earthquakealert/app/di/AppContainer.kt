package com.earthquakealert.app.di

import android.content.Context
import com.earthquakealert.app.data.local.database.EarthquakeDatabase
import com.earthquakealert.app.data.remote.api.ApiClient
import com.earthquakealert.app.data.remote.service.BmkgRemoteDataSourceImpl
import com.earthquakealert.app.data.remote.service.EarthquakeRemoteDataSource
import com.earthquakealert.app.data.repository.EarthquakeRepositoryImpl
import com.earthquakealert.app.data.repository.SettingsRepositoryImpl
import com.earthquakealert.app.domain.repository.EarthquakeRepository
import com.earthquakealert.app.domain.repository.LocationRepository
import com.earthquakealert.app.domain.repository.SettingsRepository
import com.earthquakealert.app.evaluator.AlertEvaluator
import com.earthquakealert.app.evaluator.DefaultAlertEvaluator
import com.earthquakealert.app.location.LocationRepositoryImpl
import com.earthquakealert.app.notification.NotificationService
import com.earthquakealert.app.notification.NotificationServiceImpl

interface AppContainer {
    val earthquakeRepository: EarthquakeRepository
    val locationRepository: LocationRepository
    val settingsRepository: SettingsRepository
    val alertEvaluator: AlertEvaluator
    val notificationService: NotificationService
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: EarthquakeDatabase by lazy {
        EarthquakeDatabase.getInstance(context)
    }

    private val remoteDataSource: EarthquakeRemoteDataSource by lazy {
        BmkgRemoteDataSourceImpl(ApiClient.bmkgApiService)
    }

    override val earthquakeRepository: EarthquakeRepository by lazy {
        EarthquakeRepositoryImpl(
            remoteDataSource = remoteDataSource,
            earthquakeDao = database.earthquakeDao(),
            processedEventDao = database.processedEventDao()
        )
    }

    override val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(context)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }

    override val alertEvaluator: AlertEvaluator by lazy {
        DefaultAlertEvaluator()
    }

    override val notificationService: NotificationService by lazy {
        NotificationServiceImpl(context)
    }
}
