package com.earthquakealert.app

import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.evaluator.DefaultAlertEvaluator
import com.earthquakealert.app.notification.NotificationService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Phase11SimulatorTest {

    private lateinit var evaluator: DefaultAlertEvaluator
    private lateinit var fakeNotificationService: FakeNotificationService

    // Yogyakarta simulation coordinates
    private val simulatedUserLocation = UserLocation(
        latitude = -7.7956,
        longitude = 110.3695
    )

    @Before
    fun setup() {
        evaluator = DefaultAlertEvaluator()
        fakeNotificationService = FakeNotificationService()
    }

    @Test
    fun testMajorNearbySimulationTriggersHighAlert() {
        val simulatedQuake = Earthquake(
            id = "sim-preset-1",
            timestampMillis = System.currentTimeMillis(),
            latitude = -7.7956,
            longitude = 110.3695 + (25.0 / 111.0), // ~25 km away
            magnitude = 6.2,
            depthKm = 10.0,
            region = "Yogyakarta Fault Region",
            tsunamiPotential = false,
            mmi = "V MMI"
        )

        val assessment = evaluator.evaluate(
            earthquake = simulatedQuake,
            userLocation = simulatedUserLocation,
            settings = AlertSettings()
        )

        assertTrue(assessment.shouldAlert)
        assertEquals(AlertSeverity.HIGH, assessment.severity)

        fakeNotificationService.sendEarthquakeAlert(simulatedQuake, assessment)
        assertEquals(1, fakeNotificationService.sentAlerts.size)
        assertEquals("sim-preset-1", fakeNotificationService.sentAlerts[0].first.id)
    }

    @Test
    fun testTsunamiAdvisorySimulationTriggersHighAlert() {
        val simulatedQuake = Earthquake(
            id = "sim-tsunami",
            timestampMillis = System.currentTimeMillis(),
            latitude = -7.7956,
            longitude = 110.3695 + (80.0 / 111.0), // ~80 km away
            magnitude = 7.1,
            depthKm = 20.0,
            region = "South Coast of Java",
            tsunamiPotential = true,
            mmi = "VI MMI"
        )

        val assessment = evaluator.evaluate(
            earthquake = simulatedQuake,
            userLocation = simulatedUserLocation,
            settings = AlertSettings()
        )

        assertTrue(assessment.shouldAlert)
        assertEquals(AlertSeverity.HIGH, assessment.severity)
        assertTrue(assessment.reason.contains("Tsunami advisory", ignoreCase = true))
    }

    @Test
    fun testMinorDistantSimulationProducesNoAlert() {
        val simulatedQuake = Earthquake(
            id = "sim-minor",
            timestampMillis = System.currentTimeMillis(),
            latitude = -7.7956,
            longitude = 110.3695 + (250.0 / 111.0), // ~250 km away
            magnitude = 3.6,
            depthKm = 15.0,
            region = "Distant Region",
            tsunamiPotential = false,
            mmi = null
        )

        val assessment = evaluator.evaluate(
            earthquake = simulatedQuake,
            userLocation = simulatedUserLocation,
            settings = AlertSettings()
        )

        assertEquals(false, assessment.shouldAlert)
        assertEquals(AlertSeverity.NONE, assessment.severity)
    }

    private class FakeNotificationService : NotificationService {
        val sentAlerts = mutableListOf<Pair<Earthquake, EarthquakeAssessment>>()

        override fun sendEarthquakeAlert(earthquake: Earthquake, assessment: EarthquakeAssessment) {
            sentAlerts.add(earthquake to assessment)
        }

        override fun cancelAlert(earthquakeId: String) {}
    }
}
