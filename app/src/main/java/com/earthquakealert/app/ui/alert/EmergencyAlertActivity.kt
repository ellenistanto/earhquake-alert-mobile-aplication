package com.earthquakealert.app.ui.alert

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.earthquakealert.app.notification.AlarmPlayer
import com.earthquakealert.app.ui.MainActivity
import com.earthquakealert.app.ui.theme.EarthquakeAlertTheme

class EmergencyAlertActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLockscreenFlags()

        val earthquakeId = intent.getStringExtra(EXTRA_EARTHQUAKE_ID) ?: "alert-01"
        val magnitude = intent.getDoubleExtra(EXTRA_MAGNITUDE, 5.0)
        val distanceKm = intent.getDoubleExtra(EXTRA_DISTANCE_KM, 30.0)
        val region = intent.getStringExtra(EXTRA_REGION) ?: "Nearby Region"
        val depthKm = intent.getDoubleExtra(EXTRA_DEPTH_KM, 10.0)

        val appContainer = (application as? com.earthquakealert.app.EarthquakeApplication)?.container
            ?: com.earthquakealert.app.di.DefaultAppContainer(this)
        val notificationService = appContainer.notificationService

        setContent {
            EarthquakeAlertTheme {
                ActiveAlertScreen(
                    earthquakeId = earthquakeId,
                    magnitude = magnitude,
                    distanceKm = distanceKm,
                    region = region,
                    depthKm = depthKm,
                    onDismiss = {
                        notificationService.cancelAlert(earthquakeId)
                        notificationService.cancelAllAlerts()
                        finish()
                    },
                    onViewDetails = { id ->
                        notificationService.cancelAlert(earthquakeId)
                        notificationService.cancelAllAlerts()
                        val intent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra(MainActivity.EXTRA_NAVIGATE_TO_DETAIL_ID, id)
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmPlayer.stop()
    }

    private fun setupLockscreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        const val EXTRA_EARTHQUAKE_ID = "extra_earthquake_id"
        const val EXTRA_MAGNITUDE = "extra_magnitude"
        const val EXTRA_DISTANCE_KM = "extra_distance_km"
        const val EXTRA_REGION = "extra_region"
        const val EXTRA_DEPTH_KM = "extra_depth_km"
    }
}
