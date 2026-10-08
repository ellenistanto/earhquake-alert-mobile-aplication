package com.earthquakealert.app.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.earthquakealert.app.R
import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.EarthquakeAssessment
import com.earthquakealert.app.ui.MainActivity
import com.earthquakealert.app.ui.alert.EmergencyAlertActivity

class NotificationServiceImpl(
    private val context: Context
) : NotificationService {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    override fun sendEarthquakeAlert(
        earthquake: Earthquake,
        assessment: EarthquakeAssessment
    ) {
        if (!hasNotificationPermission()) {
            return
        }

        val isHighSeverity = assessment.severity == AlertSeverity.HIGH
        val channelId = if (isHighSeverity) CHANNEL_EMERGENCY else CHANNEL_GENERAL

        // Intent for tapping notification -> opens MainActivity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_NAVIGATE_TO_DETAIL_ID, earthquake.id)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            earthquake.id.hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isHighSeverity) {
            "EARTHQUAKE DETECTED - M %.1f".format(earthquake.magnitude)
        } else {
            "Earthquake Alert - M %.1f".format(earthquake.magnitude)
        }

        val text = "%.0f km away | %s. Take safety precautions.".format(
            assessment.distanceKm,
            earthquake.region
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(
                if (isHighSeverity) NotificationCompat.PRIORITY_MAX
                else NotificationCompat.PRIORITY_HIGH
            )
            .setCategory(
                if (isHighSeverity) NotificationCompat.CATEGORY_ALARM
                else NotificationCompat.CATEGORY_EVENT
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)

        // Add Full-Screen Intent for High Severity alerts
        if (isHighSeverity) {
            val fullScreenIntent = Intent(context, EmergencyAlertActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EmergencyAlertActivity.EXTRA_EARTHQUAKE_ID, earthquake.id)
                putExtra(EmergencyAlertActivity.EXTRA_MAGNITUDE, earthquake.magnitude)
                putExtra(EmergencyAlertActivity.EXTRA_DISTANCE_KM, assessment.distanceKm)
                putExtra(EmergencyAlertActivity.EXTRA_REGION, earthquake.region)
                putExtra(EmergencyAlertActivity.EXTRA_DEPTH_KM, earthquake.depthKm ?: 10.0)
            }
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                (earthquake.id.hashCode() + 1),
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setFullScreenIntent(fullScreenPendingIntent, true)

            // Trigger emergency alarm playback
            AlarmPlayer.play(context, loop = true, enableVibration = true)
        }

        try {
            notificationManager.notify(earthquake.id.hashCode(), builder.build())
        } catch (_: SecurityException) {
            // Gracefully handle missing permission
        }
    }

    override fun cancelAlert(earthquakeId: String) {
        AlarmPlayer.stop()
        try {
            notificationManager.cancel(earthquakeId.hashCode())
        } catch (_: Exception) {
            // Ignore failure
        }
    }

    override fun cancelAllAlerts() {
        AlarmPlayer.stop()
        try {
            notificationManager.cancelAll()
        } catch (_: Exception) {
            // Ignore failure
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notificationManager.areNotificationsEnabled()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.alarm_beep}")
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // 1. Emergency Channel
            val emergencyChannel = NotificationChannel(
                CHANNEL_EMERGENCY,
                "Earthquake Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alarms and full-screen alerts for nearby earthquakes"
                enableVibration(true)
                vibrationPattern = AlarmPlayer.EMERGENCY_VIBRATION_PATTERN
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setShowBadge(true)
                try {
                    setBypassDnd(true)
                } catch (_: Exception) {
                    // Ignored if device/OEM restricts programmatic DND bypass
                }
            }

            // 2. General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "Earthquake General Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Standard notifications for felt and moderate regional earthquakes"
                enableVibration(true)
                setShowBadge(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannels(listOf(emergencyChannel, generalChannel))
        }
    }

    companion object {
        const val CHANNEL_EMERGENCY = "earthquake_emergency"
        const val CHANNEL_GENERAL = "earthquake_general"
    }
}
