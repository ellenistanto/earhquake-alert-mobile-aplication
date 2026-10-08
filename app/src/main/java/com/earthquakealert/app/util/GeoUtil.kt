package com.earthquakealert.app.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GeoUtil {
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates great-circle distance between two coordinates in kilometers using Haversine formula.
     * Pure Kotlin function with no Android platform dependencies.
     */
    fun calculateDistanceKm(
        userLat: Double,
        userLon: Double,
        quakeLat: Double,
        quakeLon: Double
    ): Double {
        if (userLat == quakeLat && userLon == quakeLon) {
            return 0.0
        }

        val dLat = Math.toRadians(quakeLat - userLat)
        val dLon = Math.toRadians(quakeLon - userLon)

        val lat1Rad = Math.toRadians(userLat)
        val lat2Rad = Math.toRadians(quakeLat)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_KM * c
    }
}
