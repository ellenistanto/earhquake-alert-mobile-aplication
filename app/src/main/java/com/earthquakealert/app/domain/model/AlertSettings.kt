package com.earthquakealert.app.domain.model

enum class DistanceMode {
    AUTO,
    CUSTOM
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppLanguage {
    SYSTEM,
    INDONESIAN,
    ENGLISH
}

data class AlertSettings(
    val alertEnabled: Boolean = true,
    val strongEarthquakeEnabled: Boolean = true,
    val feltEarthquakeEnabled: Boolean = true,
    val distanceMode: DistanceMode = DistanceMode.AUTO,
    val customDistanceKm: Int? = 150,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val fullScreenAlertEnabled: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM
)
