package com.earthquakealert.app.evaluator

import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.DistanceMode
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.util.GeoUtil

class DefaultAlertEvaluator : AlertEvaluator {

    override fun evaluate(
        earthquake: Earthquake,
        userLocation: UserLocation?,
        settings: AlertSettings
    ): EarthquakeAssessment {
        // 1. Check global master switch
        if (!settings.alertEnabled) {
            return EarthquakeAssessment(
                earthquakeId = earthquake.id,
                distanceKm = -1.0,
                shouldAlert = false,
                severity = AlertSeverity.NONE,
                reason = "Alerts are disabled in settings"
            )
        }

        // Handle case where location is not granted/available
        if (userLocation == null) {
            val isSevere = (earthquake.tsunamiPotential == true) || (earthquake.magnitude >= AlertThresholds.MAGNITUDE_STRONG)
            val shouldAlert = isSevere && settings.strongEarthquakeEnabled
            return EarthquakeAssessment(
                earthquakeId = earthquake.id,
                distanceKm = -1.0,
                shouldAlert = shouldAlert,
                severity = if (isSevere) AlertSeverity.HIGH else AlertSeverity.NONE,
                reason = if (isSevere) "Major earthquake advisory (location unavailable)"
                else "Location unavailable for proximity check"
            )
        }

        val distanceKm = GeoUtil.calculateDistanceKm(
            userLat = userLocation.latitude,
            userLon = userLocation.longitude,
            quakeLat = earthquake.latitude,
            quakeLon = earthquake.longitude
        )


        val effectiveMaxDistanceKm = if (settings.distanceMode == DistanceMode.CUSTOM && settings.customDistanceKm != null) {
            settings.customDistanceKm.toDouble()
        } else {
            AlertThresholds.DISTANCE_REGIONAL_DEFAULT_KM
        }

        val mag = earthquake.magnitude
        val depth = earthquake.depthKm ?: 50.0
        val isShallow = depth <= AlertThresholds.DEPTH_SHALLOW_KM
        val hasFeltReport = !earthquake.mmi.isNullOrBlank()

        // 2. Evaluate based on proximity, magnitude, depth, and felt reports
        val (severity, reason) = when {
            // VERY NEAR (<= 25 km)
            distanceKm <= AlertThresholds.DISTANCE_VERY_NEAR_KM -> {
                when {
                    mag >= AlertThresholds.MAGNITUDE_LIGHT -> {
                        AlertSeverity.HIGH to "Earthquake M %.1f detected very close (%.0f km)".format(mag, distanceKm)
                    }
                    mag >= AlertThresholds.MAGNITUDE_MINOR -> {
                        AlertSeverity.WARNING to "Minor earthquake M %.1f detected very close (%.0f km)".format(mag, distanceKm)
                    }
                    else -> AlertSeverity.INFO to "Low magnitude earthquake M %.1f nearby (%.0f km)".format(mag, distanceKm)
                }
            }

            // NEAR (<= 75 km)
            distanceKm <= AlertThresholds.DISTANCE_NEAR_KM -> {
                when {
                    mag >= AlertThresholds.MAGNITUDE_MODERATE -> {
                        AlertSeverity.HIGH to "Significant earthquake M %.1f within %.0f km".format(mag, distanceKm)
                    }
                    mag >= AlertThresholds.MAGNITUDE_LIGHT -> {
                        if (isShallow || (hasFeltReport && settings.feltEarthquakeEnabled)) {
                            AlertSeverity.WARNING to "Shallow/felt earthquake M %.1f within %.0f km".format(mag, distanceKm)
                        } else {
                            AlertSeverity.INFO to "Earthquake M %.1f within %.0f km".format(mag, distanceKm)
                        }
                    }
                    else -> AlertSeverity.NONE to "Earthquake below alert threshold for this distance (%.0f km)".format(distanceKm)
                }
            }

            // REGIONAL (within user configured radius or 150 km)
            distanceKm <= effectiveMaxDistanceKm -> {
                when {
                    mag >= AlertThresholds.MAGNITUDE_STRONG -> {
                        if (settings.strongEarthquakeEnabled) {
                            AlertSeverity.HIGH to "Strong earthquake M %.1f in your region (%.0f km away)".format(mag, distanceKm)
                        } else {
                            AlertSeverity.WARNING to "Strong earthquake M %.1f in your region (%.0f km away)".format(mag, distanceKm)
                        }
                    }
                    mag >= AlertThresholds.MAGNITUDE_MODERATE -> {
                        if (isShallow && hasFeltReport && settings.feltEarthquakeEnabled) {
                            AlertSeverity.WARNING to "Moderate felt earthquake M %.1f (%.0f km away)".format(mag, distanceKm)
                        } else {
                            AlertSeverity.INFO to "Moderate earthquake M %.1f recorded (%.0f km away)".format(mag, distanceKm)
                        }
                    }
                    mag >= AlertThresholds.MAGNITUDE_LIGHT && isShallow && hasFeltReport && settings.feltEarthquakeEnabled -> {
                        AlertSeverity.INFO to "Felt earthquake M %.1f recorded in region (%.0f km away)".format(mag, distanceKm)
                    }
                    else -> AlertSeverity.NONE to "Earthquake M %.1f below alert threshold for this distance (%.0f km away)".format(mag, distanceKm)
                }
            }

            // DISTANT (> 150 km)
            else -> {
                // Major events (M >= 7.0) or strong shallow events may still warrant an informational warning
                if (mag >= AlertThresholds.MAGNITUDE_MAJOR && isShallow) {
                    AlertSeverity.WARNING to "Major earthquake M %.1f (%.0f km away)".format(mag, distanceKm)
                } else if (mag >= AlertThresholds.MAGNITUDE_STRONG && settings.strongEarthquakeEnabled && distanceKm <= 300.0) {
                    AlertSeverity.INFO to "Strong regional earthquake M %.1f (%.0f km away)".format(mag, distanceKm)
                } else {
                    AlertSeverity.NONE to "Distant earthquake outside alert radius (%.0f km away)".format(distanceKm)
                }
            }
        }

        // Tsunami override: if tsunami potential exists and is within 300 km -> at least WARNING
        val finalSeverity = if (earthquake.tsunamiPotential == true && distanceKm <= 300.0 && severity < AlertSeverity.WARNING) {
            AlertSeverity.WARNING
        } else {
            severity
        }

        val finalReason = if (earthquake.tsunamiPotential == true && distanceKm <= 300.0) {
            "$reason | Tsunami advisory in effect"
        } else {
            reason
        }

        val shouldAlert = finalSeverity != AlertSeverity.NONE

        return EarthquakeAssessment(
            earthquakeId = earthquake.id,
            distanceKm = distanceKm,
            shouldAlert = shouldAlert,
            severity = finalSeverity,
            reason = finalReason
        )
    }
}

