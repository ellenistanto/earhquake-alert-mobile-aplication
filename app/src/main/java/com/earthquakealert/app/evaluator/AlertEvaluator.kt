package com.earthquakealert.app.evaluator

import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment
import com.earthquakealert.app.domain.model.UserLocation

interface AlertEvaluator {
    fun evaluate(
        earthquake: Earthquake,
        userLocation: UserLocation?,
        settings: AlertSettings
    ): EarthquakeAssessment
}
