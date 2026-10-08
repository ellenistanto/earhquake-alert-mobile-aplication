package com.earthquakealert.app.evaluator

object AlertThresholds {
    // Distance boundaries (km)
    const val DISTANCE_VERY_NEAR_KM = 25.0
    const val VERY_NEAR_DISTANCE_KM = 25.0

    const val DISTANCE_NEAR_KM = 75.0
    const val NEAR_DISTANCE_KM = 75.0

    const val DISTANCE_REGIONAL_DEFAULT_KM = 150.0
    const val REGIONAL_DISTANCE_KM = 150.0

    // Magnitude thresholds
    const val MAGNITUDE_MINOR = 3.0
    const val MAGNITUDE_LIGHT = 4.0
    const val MAGNITUDE_MODERATE = 5.0
    const val MAGNITUDE_SIGNIFICANT = 5.0
    const val SIGNIFICANT_MAGNITUDE = 5.0
    const val MAGNITUDE_STRONG = 6.0
    const val MAGNITUDE_MAJOR = 7.0

    // Depth thresholds (km)
    const val DEPTH_SHALLOW_KM = 30.0
    const val SHALLOW_DEPTH_KM = 30.0
    const val DEPTH_INTERMEDIATE_KM = 70.0
}
