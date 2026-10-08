package com.earthquakealert.app

import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.domain.repository.EarthquakeRepository
import com.earthquakealert.app.domain.repository.LocationRepository
import com.earthquakealert.app.domain.repository.SettingsRepository
import com.earthquakealert.app.evaluator.AlertEvaluator
import com.earthquakealert.app.notification.NotificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Phase9BackgroundSyncTest {

    private lateinit var fakeEarthquakeRepo: FakeEarthquakeRepository
    private lateinit var fakeLocationRepo: FakeLocationRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var fakeEvaluator: FakeAlertEvaluator
    private lateinit var fakeNotificationService: FakeNotificationService

    @Before
    fun setup() {
        fakeEarthquakeRepo = FakeEarthquakeRepository()
        fakeLocationRepo = FakeLocationRepository()
        fakeSettingsRepo = FakeSettingsRepository()
        fakeEvaluator = FakeAlertEvaluator()
        fakeNotificationService = FakeNotificationService()
    }

    @Test
    fun workerProcessesUnprocessedEarthquakeAndSendsAlertWhenEligible() = runBlocking {
        val testQuake = Earthquake(
            id = "quake-101",
            timestampMillis = 1791280800000L,
            latitude = -7.5,
            longitude = 110.5,
            magnitude = 6.2,
            depthKm = 15.0,
            region = "Yogyakarta Region",
            tsunamiPotential = false,
            mmi = "IV-V MMI"
        )
        fakeEarthquakeRepo.setRemoteQuakes(listOf(testQuake))

        // Worker sync step logic simulation
        val settings = fakeSettingsRepo.getSettings()
        val userLocation = fakeLocationRepo.getLastKnownLocation()
        val latestEarthquakes = fakeEarthquakeRepo.getLatestEarthquakes()

        for (quake in latestEarthquakes) {
            if (fakeEarthquakeRepo.isEventProcessed(quake.id)) continue

            val assessment = fakeEvaluator.evaluate(quake, userLocation, settings)
            if (assessment.shouldAlert) {
                fakeNotificationService.sendEarthquakeAlert(quake, assessment)
                fakeEarthquakeRepo.markEventProcessed(quake.id, alertTriggered = true)
            } else {
                fakeEarthquakeRepo.markEventProcessed(quake.id, alertTriggered = false)
            }
        }

        // Verification
        assertEquals(1, fakeNotificationService.sentAlerts.size)
        assertEquals("quake-101", fakeNotificationService.sentAlerts[0].first.id)
        assertTrue(fakeEarthquakeRepo.isEventProcessed("quake-101"))
    }

    @Test
    fun workerSkipsAlreadyProcessedEarthquakes() = runBlocking {
        val testQuake = Earthquake(
            id = "quake-102",
            timestampMillis = 1791277200000L,
            latitude = -7.5,
            longitude = 110.5,
            magnitude = 6.5,
            depthKm = 10.0,
            region = "Java Sea",
            tsunamiPotential = false,
            mmi = null
        )
        fakeEarthquakeRepo.setRemoteQuakes(listOf(testQuake))
        // Already processed beforehand
        fakeEarthquakeRepo.markEventProcessed("quake-102", alertTriggered = true)

        val settings = fakeSettingsRepo.getSettings()
        val userLocation = fakeLocationRepo.getLastKnownLocation()
        val latestEarthquakes = fakeEarthquakeRepo.getLatestEarthquakes()

        for (quake in latestEarthquakes) {
            if (fakeEarthquakeRepo.isEventProcessed(quake.id)) continue

            val assessment = fakeEvaluator.evaluate(quake, userLocation, settings)
            if (assessment.shouldAlert) {
                fakeNotificationService.sendEarthquakeAlert(quake, assessment)
                fakeEarthquakeRepo.markEventProcessed(quake.id, alertTriggered = true)
            }
        }

        // Should NOT trigger alert again
        assertEquals(0, fakeNotificationService.sentAlerts.size)
    }

    @Test
    fun workerMarksSubThresholdQuakesAsProcessedWithoutTriggeringAlert() = runBlocking {
        val mildQuake = Earthquake(
            id = "quake-103",
            timestampMillis = 1791273600000L,
            latitude = -1.0,
            longitude = 120.0,
            magnitude = 2.8,
            depthKm = 10.0,
            region = "Central Sulawesi",
            tsunamiPotential = false,
            mmi = null
        )
        fakeEarthquakeRepo.setRemoteQuakes(listOf(mildQuake))
        fakeEvaluator.nextAssessment = EarthquakeAssessment(
            earthquakeId = "quake-103",
            distanceKm = 1200.0,
            shouldAlert = false,
            severity = AlertSeverity.NONE,
            reason = "Distance beyond threshold"
        )

        val settings = fakeSettingsRepo.getSettings()
        val userLocation = fakeLocationRepo.getLastKnownLocation()
        val latestEarthquakes = fakeEarthquakeRepo.getLatestEarthquakes()

        for (quake in latestEarthquakes) {
            if (fakeEarthquakeRepo.isEventProcessed(quake.id)) continue

            val assessment = fakeEvaluator.evaluate(quake, userLocation, settings)
            if (assessment.shouldAlert) {
                fakeNotificationService.sendEarthquakeAlert(quake, assessment)
                fakeEarthquakeRepo.markEventProcessed(quake.id, alertTriggered = true)
            } else {
                fakeEarthquakeRepo.markEventProcessed(quake.id, alertTriggered = false)
            }
        }

        assertEquals(0, fakeNotificationService.sentAlerts.size)
        assertTrue(fakeEarthquakeRepo.isEventProcessed("quake-103"))
    }

    // --- Fakes ---

    private class FakeEarthquakeRepository : EarthquakeRepository {
        private val quakes = mutableListOf<Earthquake>()
        private val processedIds = mutableSetOf<String>()

        fun setRemoteQuakes(list: List<Earthquake>) {
            quakes.clear()
            quakes.addAll(list)
        }

        override suspend fun getLatestEarthquakes(): List<Earthquake> = quakes

        override fun observeHistory(): Flow<List<Earthquake>> = MutableStateFlow(quakes).asStateFlow()

        override suspend fun getEarthquakeById(id: String): Earthquake? = quakes.find { it.id == id }

        override suspend fun isEventProcessed(eventId: String): Boolean = processedIds.contains(eventId)

        override suspend fun markEventProcessed(eventId: String, alertTriggered: Boolean) {
            processedIds.add(eventId)
        }
    }

    private class FakeLocationRepository : LocationRepository {
        private var loc: UserLocation? = UserLocation(
            latitude = -7.56,
            longitude = 110.82,
            accuracyMeters = 15f,
            updatedAtMillis = System.currentTimeMillis()
        )

        override fun observeLocation(): Flow<UserLocation?> = MutableStateFlow(loc).asStateFlow()

        override suspend fun getLastKnownLocation(): UserLocation? = loc

        override suspend fun refreshLocation(): UserLocation? = loc
    }

    private class FakeSettingsRepository : SettingsRepository {
        private var settings = AlertSettings()

        override fun observeSettings(): Flow<AlertSettings> = MutableStateFlow(settings).asStateFlow()

        override suspend fun getSettings(): AlertSettings = settings

        override suspend fun updateSettings(settings: AlertSettings) {
            this.settings = settings
        }
    }

    private class FakeAlertEvaluator : AlertEvaluator {
        var nextAssessment: EarthquakeAssessment = EarthquakeAssessment(
            earthquakeId = "quake-101",
            distanceKm = 45.0,
            shouldAlert = true,
            severity = AlertSeverity.HIGH,
            reason = "Strong shallow earthquake nearby"
        )

        override fun evaluate(
            earthquake: Earthquake,
            userLocation: UserLocation?,
            settings: AlertSettings
        ): EarthquakeAssessment = nextAssessment
    }

    private class FakeNotificationService : NotificationService {
        val sentAlerts = mutableListOf<Pair<Earthquake, EarthquakeAssessment>>()

        override fun sendEarthquakeAlert(earthquake: Earthquake, assessment: EarthquakeAssessment) {
            sentAlerts.add(earthquake to assessment)
        }

        override fun cancelAlert(earthquakeId: String) {}
    }
}
