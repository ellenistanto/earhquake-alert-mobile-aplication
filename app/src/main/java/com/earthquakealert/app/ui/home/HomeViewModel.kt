package com.earthquakealert.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.domain.repository.EarthquakeRepository
import com.earthquakealert.app.domain.repository.LocationRepository
import com.earthquakealert.app.domain.repository.SettingsRepository
import com.earthquakealert.app.evaluator.AlertEvaluator
import com.earthquakealert.app.ui.components.ProtectionStatus
import com.earthquakealert.app.util.GeoUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EarthquakeWithDistance(
    val earthquake: Earthquake,
    val distanceKm: Double?
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val protectionStatus: ProtectionStatus = ProtectionStatus.ACTIVE,
    val userLocation: UserLocation? = null,
    val latestEarthquake: Earthquake? = null,
    val latestDistanceKm: Double? = null,
    val latestAssessment: EarthquakeAssessment? = null,
    val recentEarthquakes: List<EarthquakeWithDistance> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(
    private val earthquakeRepository: EarthquakeRepository,
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository,
    private val alertEvaluator: AlertEvaluator
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
        refresh()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                earthquakeRepository.observeHistory(),
                locationRepository.observeLocation(),
                settingsRepository.observeSettings()
            ) { history, location, settings ->
                val status = calculateProtectionStatus(settings, location)
                val latest = history.firstOrNull()

                val latestDistance = if (latest != null && location != null) {
                    GeoUtil.calculateDistanceKm(
                        location.latitude,
                        location.longitude,
                        latest.latitude,
                        latest.longitude
                    )
                } else null

                val assessment = if (latest != null) {
                    alertEvaluator.evaluate(latest, location, settings)
                } else null

                val recents = history.drop(1).take(5).map { quake ->
                    val dist = if (location != null) {
                        GeoUtil.calculateDistanceKm(
                            location.latitude,
                            location.longitude,
                            quake.latitude,
                            quake.longitude
                        )
                    } else null
                    EarthquakeWithDistance(quake, dist)
                }

                HomeUiState(
                    isLoading = false,
                    protectionStatus = status,
                    userLocation = location,
                    latestEarthquake = latest,
                    latestDistanceKm = latestDistance,
                    latestAssessment = assessment,
                    recentEarthquakes = recents,
                    errorMessage = null
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                locationRepository.getLastKnownLocation()
                earthquakeRepository.getLatestEarthquakes()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unable to refresh earthquake data: ${e.message}"
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun calculateProtectionStatus(
        settings: AlertSettings,
        location: UserLocation?
    ): ProtectionStatus {
        return when {
            !settings.alertEnabled -> ProtectionStatus.DISABLED
            location == null -> ProtectionStatus.LIMITED
            else -> ProtectionStatus.ACTIVE
        }
    }

}
