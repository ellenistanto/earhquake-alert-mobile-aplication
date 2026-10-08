package com.earthquakealert.app

import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.DistanceMode
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.evaluator.AlertThresholds
import com.earthquakealert.app.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class Phase1SkeletonTest {

    @Test
    fun testDomainModelsInitialization() {
        val earthquake = Earthquake(
            id = "eq-test-01",
            timestampMillis = 1710000000000L,
            latitude = -7.82,
            longitude = 110.42,
            magnitude = 5.4,
            depthKm = 10.0,
            region = "Southern Java",
            tsunamiPotential = false,
            mmi = "II-III",
            source = "BMKG"
        )

        assertEquals("eq-test-01", earthquake.id)
        assertEquals(5.4, earthquake.magnitude, 0.01)
        assertEquals("BMKG", earthquake.source)
    }

    @Test
    fun testSettingsDefaultValues() {
        val settings = AlertSettings()
        assertEquals(true, settings.alertEnabled)
        assertEquals(DistanceMode.AUTO, settings.distanceMode)
        assertEquals(true, settings.soundEnabled)
    }

    @Test
    fun testAlertThresholdsExist() {
        assertEquals(25.0, AlertThresholds.VERY_NEAR_DISTANCE_KM, 0.001)
        assertEquals(75.0, AlertThresholds.NEAR_DISTANCE_KM, 0.001)
        assertEquals(5.0, AlertThresholds.SIGNIFICANT_MAGNITUDE, 0.001)
    }

    @Test
    fun testNavigationRoutes() {
        assertEquals("onboarding", Screen.Onboarding.route)
        assertEquals("home", Screen.Home.route)
        assertEquals("alert", Screen.ActiveAlert.route)
        assertEquals("detail/123", Screen.Detail.createRoute("123"))
    }
}
