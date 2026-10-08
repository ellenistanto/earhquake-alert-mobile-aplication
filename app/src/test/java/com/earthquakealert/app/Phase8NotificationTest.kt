package com.earthquakealert.app

import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment
import com.earthquakealert.app.notification.AlarmPlayer
import com.earthquakealert.app.notification.NotificationService
import com.earthquakealert.app.notification.NotificationServiceImpl
import com.earthquakealert.app.ui.alert.EmergencyAlertActivity
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase8NotificationTest {

    @Test
    fun testNotificationChannelConstants() {
        assertEquals("earthquake_emergency", NotificationServiceImpl.CHANNEL_EMERGENCY)
        assertEquals("earthquake_general", NotificationServiceImpl.CHANNEL_GENERAL)
    }

    @Test
    fun testVibrationPatternFormat() {
        val pattern = AlarmPlayer.EMERGENCY_VIBRATION_PATTERN
        val expected = longArrayOf(0, 500, 300, 500, 300, 500)
        assertArrayEquals(expected, pattern)
    }

    @Test
    fun testEmergencyActivityExtraKeys() {
        assertEquals("extra_earthquake_id", EmergencyAlertActivity.EXTRA_EARTHQUAKE_ID)
        assertEquals("extra_magnitude", EmergencyAlertActivity.EXTRA_MAGNITUDE)
        assertEquals("extra_distance_km", EmergencyAlertActivity.EXTRA_DISTANCE_KM)
        assertEquals("extra_region", EmergencyAlertActivity.EXTRA_REGION)
        assertEquals("extra_depth_km", EmergencyAlertActivity.EXTRA_DEPTH_KM)
    }

    @Test
    fun testNotificationServiceMockContract() {
        var alertSent = false
        var sentSeverity: AlertSeverity? = null

        val fakeNotificationService = object : NotificationService {
            override fun sendEarthquakeAlert(
                earthquake: Earthquake,
                assessment: EarthquakeAssessment
            ) {
                alertSent = true
                sentSeverity = assessment.severity
            }

            override fun cancelAlert(earthquakeId: String) {
                alertSent = false
            }
        }

        val testQuake = Earthquake(
            id = "test-alert-01",
            timestampMillis = System.currentTimeMillis(),
            latitude = -7.8,
            longitude = 110.3,
            magnitude = 5.8,
            depthKm = 10.0,
            region = "Yogyakarta",
            tsunamiPotential = false,
            mmi = "IV MMI"
        )
        val assessment = EarthquakeAssessment(
            earthquakeId = testQuake.id,
            distanceKm = 22.0,
            shouldAlert = true,
            severity = AlertSeverity.HIGH,
            reason = "High emergency"
        )

        fakeNotificationService.sendEarthquakeAlert(testQuake, assessment)
        assertTrue(alertSent)
        assertEquals(AlertSeverity.HIGH, sentSeverity)

        fakeNotificationService.cancelAlert(testQuake.id)
        assertEquals(false, alertSent)
    }
}
