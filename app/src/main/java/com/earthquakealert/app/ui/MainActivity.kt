package com.earthquakealert.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.earthquakealert.app.EarthquakeApplication
import com.earthquakealert.app.di.DefaultAppContainer
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.AppThemeMode
import com.earthquakealert.app.navigation.EarthquakeApp
import com.earthquakealert.app.ui.theme.EarthquakeAlertTheme

import androidx.compose.runtime.CompositionLocalProvider
import com.earthquakealert.app.ui.i18n.AppStrings
import com.earthquakealert.app.ui.i18n.LocalAppStrings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialDetailId = intent.getStringExtra(EXTRA_NAVIGATE_TO_DETAIL_ID)

        setContent {
            val container = remember {
                val app = applicationContext as? EarthquakeApplication
                app?.container ?: DefaultAppContainer(applicationContext)
            }
            val settings by container.settingsRepository.observeSettings().collectAsState(initial = AlertSettings())
            val systemDark = isSystemInDarkTheme()
            val isDark = when (settings.themeMode) {
                AppThemeMode.SYSTEM -> systemDark
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            val strings = remember(settings.language) {
                AppStrings.resolve(settings.language)
            }

            CompositionLocalProvider(LocalAppStrings provides strings) {
                EarthquakeAlertTheme(darkTheme = isDark) {
                    EarthquakeApp(initialEarthquakeId = initialDetailId)
                }
            }
        }
    }

    companion object {
        const val EXTRA_NAVIGATE_TO_DETAIL_ID = "extra_navigate_to_detail_id"
    }
}
