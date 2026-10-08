package com.earthquakealert.app

import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.DistanceMode
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.evaluator.DefaultAlertEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase7AlertEvaluatorTest {

    private val evaluator = DefaultAlertEvaluator()

    // Fixed user location at Yogyakarta: -7.7956, 110.3695
    private val userLocation = UserLocation(
        latitude = -7.7956,
        longitude = 110.3695
    )

    @Test
    fun testStrongAndCloseEventTriggersHighAlert() {
        // ~15 km away, M 5.5, depth 10 km
        val closeQuake = Earthquake(
            id = "close-01",
            timestampMillis = System.currentTimeMillis(),
            latitude = -7.68,
            longitude = 110.38,
            magnitude = 5.5,
            depthKm = 10.0,
            region = "Near Yogyakarta",
            tsunamiPotential = false,
            mmi = "V MMI"
        )

        val assessment = evaluator.evaluate(closeQuake, userLocation, AlertSettings())
        assertTrue(assessment.shouldAlert)
        assertEquals(AlertSeverity.HIGH, assessment.severity)
        assertTrue(assessment.distanceKm <= 25.0)
    }

    @Test
    fun testWeakAndDistantEventTriggersNoAlert() {
        // Papua: ~3000 km away, M 4.2
        val distantQuake = Earthquake(
            id = "distant-01",
            timestampMillis = System.currentTimeMillis(),
            latitude = -2.5,
            longitude = 140.0,
            magnitude = 4.2,
            depthKm = 10.0,
            region = "Papua",
            tsunamiPotential = false,
            mmi = null
        )

        val assessment = evaluator.evaluate(distantQuake, userLocation, AlertSettings())
        assertFalse(assessment.shouldAlert)
        assertEquals(AlertSeverity.NONE, assessment.severity)
    }

    @Test
    fun testSettingsDisabledSuppressesAlert() {
        val closeQuake = Earthquake(
            id = "close-02",
            timestampMillis = System.currentTimeMillis(),
            latitude = -7.79,
            longitude = 110.36,
            magnitude = 6.0,
            depthKm = 10.0,
            region = "Yogyakarta City",
            tsunamiPotential = false,
            mmi = null
        )

        val disabledSettings = AlertSettings(alertEnabled = false)
        val assessment = evaluator.evaluate(closeQuake, userLocation, disabledSettings)
        assertFalse(assessment.shouldAlert)
        assertEquals(AlertSeverity.NONE, assessment.severity)
    }

    @Test
    fun testCustomDistanceThreshold() {
        // ~100 km away, M 4.8
        val moderateQuake = Earthquake(
            id = "mod-01",
            timestampMillis = System.currentTimeMillis(),
            latitude = -7.0,
            longitude = 110.4,
            magnitude = 4.8,
            depthKm = 15.0,
            region = "Central Java",
            tsunamiPotential = false,
            mmi = "II-III MMI"
        )

        // Custom distance 50 km: quake at ~100 km should be ignored
        val tightSettings = AlertSettings(
            distanceMode = DistanceMode.CUSTOM,
            customDistanceKm = 50
        )
        val tightAssessment = evaluator.evaluate(moderateQuake, userLocation, tightSettings)
        assertFalse(tightAssessment.shouldAlert)

        // Custom distance 120 km: quake at ~100 km should be evaluated
        val wideSettings = AlertSettings(
            distanceMode = DistanceMode.CUSTOM,
            customDistanceKm = 120
        )
        val wideAssessment = evaluator.evaluate(moderateQuake, userLocation, wideSettings)
        assertTrue(wideAssessment.shouldAlert)
    }

    @Test
    fun testTsunamiPotentialElevatesSeverity() {
        // ~200 km away, M 6.5 with tsunami potential
        val tsunamiQuake = Earthquake(
            id = "tsunami-01",
            timestampMillis = System.currentTimeMillis(),
            latitude = -9.2,
            longitude = 110.3,
            magnitude = 6.5,
            depthKm = 20.0,
            region = "Indian Ocean South of Java",
            tsunamiPotential = true,
            mmi = null
        )

        val assessment = evaluator.evaluate(tsunamiQuake, userLocation, AlertSettings())
        assertTrue(assessment.shouldAlert)
        assertTrue(assessment.severity >= AlertSeverity.WARNING)
    }
}
