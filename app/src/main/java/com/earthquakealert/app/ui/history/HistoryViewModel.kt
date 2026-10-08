package com.earthquakealert.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.domain.repository.EarthquakeRepository
import com.earthquakealert.app.domain.repository.LocationRepository
import com.earthquakealert.app.util.GeoUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HistoryFilter {
    ALL,
    FELT,
    STRONG
}

data class HistoryItemUiModel(
    val id: String,
    val magnitude: Double,
    val region: String,
    val depthKm: Double?,
    val distanceKm: Double?,
    val timestampMillis: Long,
    val mmi: String?,
    val potentialTsunami: Boolean?
)

data class HistoryUiState(
    val isLoading: Boolean = false,
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val earthquakes: List<HistoryItemUiModel> = emptyList(),
    val errorMessage: String? = null
)

class HistoryViewModel(
    private val earthquakeRepository: EarthquakeRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val filterFlow = MutableStateFlow(HistoryFilter.ALL)

    init {
        observeData()
        refresh()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                earthquakeRepository.observeHistory(),
                locationRepository.observeLocation(),
                filterFlow
            ) { history, location, filter ->
                val filtered = when (filter) {
                    HistoryFilter.ALL -> history
                    HistoryFilter.FELT -> history.filter { !it.mmi.isNullOrBlank() }
                    HistoryFilter.STRONG -> history.filter { it.magnitude >= 5.0 }
                }

                val items = filtered.map { quake ->
                    val dist = if (location != null) {
                        GeoUtil.calculateDistanceKm(
                            location.latitude,
                            location.longitude,
                            quake.latitude,
                            quake.longitude
                        )
                    } else null

                    HistoryItemUiModel(
                        id = quake.id,
                        magnitude = quake.magnitude,
                        region = quake.region,
                        depthKm = quake.depthKm,
                        distanceKm = dist,
                        timestampMillis = quake.timestampMillis,
                        mmi = quake.mmi,
                        potentialTsunami = quake.tsunamiPotential
                    )
                }

                HistoryUiState(
                    isLoading = false,
                    selectedFilter = filter,
                    earthquakes = items,
                    errorMessage = null
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun setFilter(filter: HistoryFilter) {
        filterFlow.value = filter
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                earthquakeRepository.getLatestEarthquakes()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unable to fetch earthquake history: ${e.message}"
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
