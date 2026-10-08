package com.earthquakealert.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.AppLanguage
import com.earthquakealert.app.domain.model.AppThemeMode
import com.earthquakealert.app.domain.model.DistanceMode
import com.earthquakealert.app.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SettingsRepositoryImpl(
    context: Context,
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
) : SettingsRepository {

    override fun observeSettings(): Flow<AlertSettings> = callbackFlow {
        // Emit initial value
        trySend(readSettings())

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(readSettings())
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override suspend fun getSettings(): AlertSettings {
        return readSettings()
    }

    private fun readSettings(): AlertSettings {
        val distanceModeStr = prefs.getString(KEY_DISTANCE_MODE, DistanceMode.AUTO.name) ?: DistanceMode.AUTO.name
        val distanceMode = try {
            DistanceMode.valueOf(distanceModeStr)
        } catch (_: Exception) {
            DistanceMode.AUTO
        }

        val themeModeStr = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        val themeMode = try {
            AppThemeMode.valueOf(themeModeStr)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }

        val languageStr = prefs.getString(KEY_APP_LANGUAGE, AppLanguage.SYSTEM.name) ?: AppLanguage.SYSTEM.name
        val language = try {
            AppLanguage.valueOf(languageStr)
        } catch (_: Exception) {
            AppLanguage.SYSTEM
        }

        return AlertSettings(
            alertEnabled = prefs.getBoolean(KEY_ALERT_ENABLED, true),
            strongEarthquakeEnabled = prefs.getBoolean(KEY_STRONG_ENABLED, true),
            feltEarthquakeEnabled = prefs.getBoolean(KEY_FELT_ENABLED, true),
            distanceMode = distanceMode,
            customDistanceKm = prefs.getInt(KEY_CUSTOM_DISTANCE_KM, 150),
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true),
            fullScreenAlertEnabled = prefs.getBoolean(KEY_FULL_SCREEN_ALERT, true),
            themeMode = themeMode,
            language = language
        )
    }


    override suspend fun updateSettings(settings: AlertSettings) {
        prefs.edit()
            .putBoolean(KEY_ALERT_ENABLED, settings.alertEnabled)
            .putBoolean(KEY_STRONG_ENABLED, settings.strongEarthquakeEnabled)
            .putBoolean(KEY_FELT_ENABLED, settings.feltEarthquakeEnabled)
            .putString(KEY_DISTANCE_MODE, settings.distanceMode.name)
            .putInt(KEY_CUSTOM_DISTANCE_KM, settings.customDistanceKm ?: 150)
            .putBoolean(KEY_SOUND_ENABLED, settings.soundEnabled)
            .putBoolean(KEY_VIBRATION_ENABLED, settings.vibrationEnabled)
            .putBoolean(KEY_FULL_SCREEN_ALERT, settings.fullScreenAlertEnabled)
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .putString(KEY_APP_LANGUAGE, settings.language.name)
            .apply()
    }

    companion object {
        const val PREFS_NAME = "earthquake_alert_prefs"
        const val KEY_ALERT_ENABLED = "alert_enabled"
        const val KEY_STRONG_ENABLED = "strong_earthquake_enabled"
        const val KEY_FELT_ENABLED = "felt_earthquake_enabled"
        const val KEY_DISTANCE_MODE = "distance_mode"
        const val KEY_CUSTOM_DISTANCE_KM = "custom_distance_km"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        const val KEY_FULL_SCREEN_ALERT = "full_screen_alert_enabled"
        const val KEY_THEME_MODE = "app_theme_mode"
        const val KEY_APP_LANGUAGE = "app_language"
    }
}
