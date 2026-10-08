package com.earthquakealert.app.domain.model

data class EarthquakeAssessment(
    val earthquakeId: String,
    val distanceKm: Double,
    val shouldAlert: Boolean,
    val severity: AlertSeverity,
    val reason: String
)
