package com.earthquakealert.app

import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.AppLanguage
import com.earthquakealert.app.domain.model.DistanceMode
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.domain.repository.EarthquakeRepository
import com.earthquakealert.app.domain.repository.LocationRepository
import com.earthquakealert.app.domain.repository.SettingsRepository
import com.earthquakealert.app.evaluator.AlertEvaluator
import com.earthquakealert.app.ui.components.ProtectionStatus
import com.earthquakealert.app.ui.detail.EarthquakeDetailViewModel
import com.earthquakealert.app.ui.history.HistoryFilter
import com.earthquakealert.app.ui.history.HistoryViewModel
import com.earthquakealert.app.ui.home.HomeViewModel
import com.earthquakealert.app.ui.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class Phase10ViewModelsTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeEarthquakeRepo: FakeEarthquakeRepository
    private lateinit var fakeLocationRepo: FakeLocationRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var fakeEvaluator: FakeAlertEvaluator

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeEarthquakeRepo = FakeEarthquakeRepository()
        fakeLocationRepo = FakeLocationRepository()
        fakeSettingsRepo = FakeSettingsRepository()
        fakeEvaluator = FakeAlertEvaluator()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testHomeViewModelProtectionStatusActiveWhenLocationAndAlertEnabled() = runTest {
        val vm = HomeViewModel(
            fakeEarthquakeRepo,
            fakeLocationRepo,
            fakeSettingsRepo,
            fakeEvaluator
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(ProtectionStatus.ACTIVE, state.protectionStatus)
        assertNotNull(state.latestEarthquake)
        assertEquals("q1", state.latestEarthquake?.id)
    }

    @Test
    fun testHomeViewModelProtectionStatusLimitedWhenLocationNull() = runTest {
        fakeLocationRepo.setLocation(null)

        val vm = HomeViewModel(
            fakeEarthquakeRepo,
            fakeLocationRepo,
            fakeSettingsRepo,
            fakeEvaluator
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(ProtectionStatus.LIMITED, state.protectionStatus)
    }

    @Test
    fun testHomeViewModelProtectionStatusDisabledWhenAlertsTurnedOff() = runTest {
        fakeSettingsRepo.updateSettings(AlertSettings(alertEnabled = false))

        val vm = HomeViewModel(
            fakeEarthquakeRepo,
            fakeLocationRepo,
            fakeSettingsRepo,
            fakeEvaluator
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(ProtectionStatus.DISABLED, state.protectionStatus)
    }

    @Test
    fun testHistoryViewModelFiltering() = runTest {
        val vm = HistoryViewModel(fakeEarthquakeRepo, fakeLocationRepo)
        advanceUntilIdle()

        // Default ALL filter
        assertEquals(3, vm.uiState.value.earthquakes.size)

        // Filter by FELT (mmi not null)
        vm.setFilter(HistoryFilter.FELT)
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.earthquakes.size)
        assertEquals("q1", vm.uiState.value.earthquakes[0].id)

        // Filter by STRONG (magnitude >= 5.0)
        vm.setFilter(HistoryFilter.STRONG)
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.earthquakes.size)
    }

    @Test
    fun testEarthquakeDetailViewModelLoadsQuakeAndGeneratesRecommendation() = runTest {
        val vm = EarthquakeDetailViewModel(fakeEarthquakeRepo, fakeLocationRepo)
        vm.loadDetail("q1")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertNotNull(state.earthquake)
        assertEquals("q1", state.earthquake?.id)
        assertNotNull(state.distanceKm)
        assertTrue(state.safetyRecommendation.isNotEmpty())
    }

    @Test
    fun testSettingsViewModelUpdatesSettings() = runTest {
        val vm = SettingsViewModel(fakeSettingsRepo)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.settings.alertEnabled)

        vm.toggleAlertEnabled(false)
        advanceUntilIdle()
        assertFalse(vm.uiState.value.settings.alertEnabled)

        vm.setDistanceMode(DistanceMode.CUSTOM)
        advanceUntilIdle()
        assertEquals(DistanceMode.CUSTOM, vm.uiState.value.settings.distanceMode)
    }

    @Test
    fun testSettingsViewModelUpdatesLanguage() = runTest {
        val vm = SettingsViewModel(fakeSettingsRepo)
        advanceUntilIdle()

        assertEquals(AppLanguage.SYSTEM, vm.uiState.value.settings.language)

        vm.setLanguage(AppLanguage.ENGLISH)
        advanceUntilIdle()
        assertEquals(AppLanguage.ENGLISH, vm.uiState.value.settings.language)

        vm.setLanguage(AppLanguage.INDONESIAN)
        advanceUntilIdle()
        assertEquals(AppLanguage.INDONESIAN, vm.uiState.value.settings.language)
    }

    // --- Fakes ---

    private class FakeEarthquakeRepository : EarthquakeRepository {
        private val list = listOf(
            Earthquake(
                id = "q1",
                timestampMillis = 1791280800000L,
                latitude = -7.5,
                longitude = 110.5,
                magnitude = 5.6,
                depthKm = 10.0,
                region = "South of Yogyakarta",
                tsunamiPotential = false,
                mmi = "IV MMI"
            ),
            Earthquake(
                id = "q2",
                timestampMillis = 1791277200000L,
                latitude = -8.0,
                longitude = 112.0,
                magnitude = 5.1,
                depthKm = 15.0,
                region = "East Java Coast",
                tsunamiPotential = false,
                mmi = null
            ),
            Earthquake(
                id = "q3",
                timestampMillis = 1791273600000L,
                latitude = -6.5,
                longitude = 105.5,
                magnitude = 4.2,
                depthKm = 20.0,
                region = "Sunda Strait",
                tsunamiPotential = false,
                mmi = null
            )
        )
        private val flow = MutableStateFlow(list)

        override suspend fun getLatestEarthquakes(): List<Earthquake> = list
        override fun observeHistory(): Flow<List<Earthquake>> = flow.asStateFlow()
        override suspend fun getEarthquakeById(id: String): Earthquake? = list.find { it.id == id }
        override suspend fun isEventProcessed(eventId: String): Boolean = false
        override suspend fun markEventProcessed(eventId: String, alertTriggered: Boolean) {}
    }

    private class FakeLocationRepository : LocationRepository {
        private var loc: UserLocation? = UserLocation(
            latitude = -7.8,
            longitude = 110.4,
            accuracyMeters = 10f,
            updatedAtMillis = System.currentTimeMillis()
        )
        private val flow = MutableStateFlow(loc)

        fun setLocation(newLoc: UserLocation?) {
            loc = newLoc
            flow.value = newLoc
        }

        override fun observeLocation(): Flow<UserLocation?> = flow.asStateFlow()
        override suspend fun getLastKnownLocation(): UserLocation? = loc
        override suspend fun refreshLocation(): UserLocation? = loc
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val flow = MutableStateFlow(AlertSettings())

        override fun observeSettings(): Flow<AlertSettings> = flow.asStateFlow()
        override suspend fun getSettings(): AlertSettings = flow.value
        override suspend fun updateSettings(settings: AlertSettings) {
            flow.value = settings
        }
    }

    private class FakeAlertEvaluator : AlertEvaluator {
        override fun evaluate(
            earthquake: Earthquake,
            userLocation: UserLocation?,
            settings: AlertSettings
        ): EarthquakeAssessment {
            return EarthquakeAssessment(
                earthquakeId = earthquake.id,
                distanceKm = 40.0,
                shouldAlert = settings.alertEnabled && earthquake.magnitude >= 5.0,
                severity = AlertSeverity.HIGH,
                reason = "Strong local earthquake"
            )
        }
    }
}
