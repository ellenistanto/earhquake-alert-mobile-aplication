package com.earthquakealert.app.notification

import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment

interface NotificationService {
    fun sendEarthquakeAlert(
        earthquake: Earthquake,
        assessment: EarthquakeAssessment
    )
    fun cancelAlert(earthquakeId: String)
    fun cancelAllAlerts() {
        AlarmPlayer.stop()
    }
}
