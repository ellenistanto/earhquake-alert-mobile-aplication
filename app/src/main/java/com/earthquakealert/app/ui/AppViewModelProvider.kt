package com.earthquakealert.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.earthquakealert.app.di.AppContainer
import com.earthquakealert.app.ui.detail.EarthquakeDetailViewModel
import com.earthquakealert.app.ui.history.HistoryViewModel
import com.earthquakealert.app.ui.home.HomeViewModel
import com.earthquakealert.app.ui.settings.SettingsViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(
                    container.earthquakeRepository,
                    container.locationRepository,
                    container.settingsRepository,
                    container.alertEvaluator
                ) as T

            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(
                    container.earthquakeRepository,
                    container.locationRepository
                ) as T

            modelClass.isAssignableFrom(EarthquakeDetailViewModel::class.java) ->
                EarthquakeDetailViewModel(
                    container.earthquakeRepository,
                    container.locationRepository
                ) as T

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(
                    container.settingsRepository
                ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
