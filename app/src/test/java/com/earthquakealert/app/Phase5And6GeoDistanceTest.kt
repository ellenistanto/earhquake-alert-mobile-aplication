package com.earthquakealert.app

import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.util.GeoUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase5And6GeoDistanceTest {

    @Test
    fun testSameCoordinatesReturnsZero() {
        val lat = -7.7956
        val lon = 110.3695
        val distance = GeoUtil.calculateDistanceKm(lat, lon, lat, lon)
        assertEquals(0.0, distance, 0.0001)
    }

    @Test
    fun testKnownCoordinatePairJakartaToBandung() {
        // Jakarta: -6.2088, 106.8456
        // Bandung: -6.9175, 107.6191
        // Exact great-circle Haversine distance is ~ 116.24 km
        val distance = GeoUtil.calculateDistanceKm(-6.2088, 106.8456, -6.9175, 107.6191)
        assertEquals(116.24, distance, 0.5)
    }

    @Test
    fun testKnownCoordinatePairJakartaToYogyakarta() {
        // Jakarta: -6.2088, 106.8456
        // Yogyakarta: -7.7956, 110.3695
        // Exact great-circle Haversine distance is ~ 427.06 km
        val distance = GeoUtil.calculateDistanceKm(-6.2088, 106.8456, -7.7956, 110.3695)
        assertEquals(427.06, distance, 0.5)
    }

    @Test
    fun testVeryShortDistance() {
        // 0.01 degree latitude difference is ~ 1.11 km
        val distance = GeoUtil.calculateDistanceKm(0.0, 100.0, 0.01, 100.0)
        assertEquals(1.11, distance, 0.05)
    }

    @Test
    fun testLargeDistance() {
        // Equator opposite sides: (0, 0) to (0, 180) -> half circumference ~ 20015 km
        val distance = GeoUtil.calculateDistanceKm(0.0, 0.0, 0.0, 180.0)
        assertEquals(20015.0, distance, 50.0)
    }

    @Test
    fun testUserLocationModel() {
        val userLoc = UserLocation(
            latitude = -7.7956,
            longitude = 110.3695,
            accuracyMeters = 15.0f,
            updatedAtMillis = 1710000000000L,
            areaName = "Yogyakarta"
        )

        assertEquals(-7.7956, userLoc.latitude, 0.0001)
        assertEquals(110.3695, userLoc.longitude, 0.0001)
        assertEquals(15.0f, userLoc.accuracyMeters ?: 0f, 0.01f)
        assertEquals("Yogyakarta", userLoc.areaName)
    }
}
