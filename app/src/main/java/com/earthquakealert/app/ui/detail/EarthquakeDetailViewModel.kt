package com.earthquakealert.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.repository.EarthquakeRepository
import com.earthquakealert.app.domain.repository.LocationRepository
import com.earthquakealert.app.util.GeoUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = false,
    val earthquake: Earthquake? = null,
    val distanceKm: Double? = null,
    val safetyRecommendation: String = "",
    val errorMessage: String? = null
)

class EarthquakeDetailViewModel(
    private val earthquakeRepository: EarthquakeRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState(isLoading = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadDetail(earthquakeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val quake = earthquakeRepository.getEarthquakeById(earthquakeId)
                if (quake == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Earthquake record not found."
                        )
                    }
                    return@launch
                }

                val location = locationRepository.getLastKnownLocation()
                val distance = if (location != null) {
                    GeoUtil.calculateDistanceKm(
                        location.latitude,
                        location.longitude,
                        quake.latitude,
                        quake.longitude
                    )
                } else null

                val recommendation = generateSafetyInstructions(quake, distance)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        earthquake = quake,
                        distanceKm = distance,
                        safetyRecommendation = recommendation,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load details: ${e.message}"
                    )
                }
            }
        }
    }

    private fun generateSafetyInstructions(quake: Earthquake, distanceKm: Double?): String {
        val isNearby = distanceKm != null && distanceKm < 150.0

        return when {
            quake.tsunamiPotential == true ->
                "Tsunami advisory: Move immediately to higher ground away from coastal areas. Do not wait for official warnings if strong shaking was felt near the coast."
            quake.magnitude >= 6.0 && isNearby ->
                "Significant shaking possible: Drop, Cover, and Hold On. Stay away from windows, unanchored heavy furniture, and electrical fixtures. Evacuate calmly when shaking stops."
            quake.magnitude >= 5.0 && isNearby ->
                "Moderate shaking: Stay calm and protect your head. Be prepared for potential aftershocks and check for damaged gas or electrical lines."
            else ->
                "Normal monitoring: No immediate action required. Stay informed through official BMKG announcements."
        }
    }
}
