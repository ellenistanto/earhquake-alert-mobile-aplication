package com.earthquakealert.app

import android.app.Application
import com.earthquakealert.app.di.AppContainer
import com.earthquakealert.app.di.DefaultAppContainer
import com.earthquakealert.app.sync.SyncScheduler

class EarthquakeApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        // Initialize background earthquake monitoring
        SyncScheduler.schedulePeriodicSync(this)
    }
}
