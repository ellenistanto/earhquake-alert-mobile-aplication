package com.earthquakealert.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.AppThemeMode
import com.earthquakealert.app.domain.model.DistanceMode
import com.earthquakealert.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AlertSettings = AlertSettings(),
    val isLoading: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { newSettings ->
                _uiState.value = _uiState.value.copy(settings = newSettings)
            }
        }
    }

    fun toggleAlertEnabled(enabled: Boolean) {
        updateSettings { it.copy(alertEnabled = enabled) }
    }

    fun toggleStrongEarthquakeEnabled(enabled: Boolean) {
        updateSettings { it.copy(strongEarthquakeEnabled = enabled) }
    }

    fun toggleFeltEarthquakeEnabled(enabled: Boolean) {
        updateSettings { it.copy(feltEarthquakeEnabled = enabled) }
    }

    fun setDistanceMode(mode: DistanceMode) {
        updateSettings { it.copy(distanceMode = mode) }
    }

    fun setCustomDistanceKm(distanceKm: Int) {
        updateSettings { it.copy(customDistanceKm = distanceKm) }
    }

    fun toggleSoundEnabled(enabled: Boolean) {
        updateSettings { it.copy(soundEnabled = enabled) }
    }

    fun toggleVibrationEnabled(enabled: Boolean) {
        updateSettings { it.copy(vibrationEnabled = enabled) }
    }

    fun toggleFullScreenAlertEnabled(enabled: Boolean) {
        updateSettings { it.copy(fullScreenAlertEnabled = enabled) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        updateSettings { it.copy(themeMode = mode) }
    }

    fun setLanguage(language: com.earthquakealert.app.domain.model.AppLanguage) {
        updateSettings { it.copy(language = language) }
    }

    private fun updateSettings(transform: (AlertSettings) -> AlertSettings) {
        viewModelScope.launch {
            val current = _uiState.value.settings
            val updated = transform(current)
            settingsRepository.updateSettings(updated)
        }
    }
}
