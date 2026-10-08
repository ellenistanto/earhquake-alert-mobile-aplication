package com.earthquakealert.app.notification

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.earthquakealert.app.R

object AlarmPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var audioManager: AudioManager? = null
    private var focusRequest: AudioFocusRequest? = null

    val EMERGENCY_VIBRATION_PATTERN = longArrayOf(0, 500, 300, 500, 300, 500)

    @Synchronized
    fun play(context: Context, loop: Boolean = true, enableVibration: Boolean = true) {
        stop()

        val appContext = context.applicationContext
        val am = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        audioManager = am

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // 1. Ensure alarm volume is audible if it was muted to 0
        try {
            if (am != null) {
                val currentVol = am.getStreamVolume(AudioManager.STREAM_ALARM)
                if (currentVol == 0) {
                    val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                    val targetVol = (maxVol * 0.7).toInt().coerceAtLeast(1)
                    am.setStreamVolume(AudioManager.STREAM_ALARM, targetVol, 0)
                }
            }
        } catch (_: Exception) {
            // Tolerate permission restriction on modifying volume under DND
        }

        // 2. Request transient audio focus for alarm
        try {
            if (am != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                    .setAudioAttributes(audioAttributes)
                    .setOnAudioFocusChangeListener { /* No-op: emergency alarm takes priority */ }
                    .build()
                focusRequest = req
                am.requestAudioFocus(req)
            }
        } catch (_: Exception) {
            // Audio focus failure should not prevent playback attempt
        }

        // 3. Audio playback with USAGE_ALARM to bypass DND
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                var rawLoaded = false
                try {
                    val afd = appContext.resources.openRawResourceFd(R.raw.alarm_beep)
                    if (afd != null) {
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                        rawLoaded = true
                    }
                } catch (_: Exception) {
                    rawLoaded = false
                }

                if (!rawLoaded) {
                    val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    setDataSource(appContext, alarmUri)
                }

                prepare()
            }

            player.isLooping = loop
            player.start()
            mediaPlayer = player
        } catch (_: Exception) {
            // Tolerate audio failure gracefully
        }

        // 4. Vibration
        if (enableVibration) {
            try {
                val vib = getVibrator(appContext)
                vibrator = vib
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(
                        EMERGENCY_VIBRATION_PATTERN,
                        if (loop) 0 else -1
                    )
                    vib.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(EMERGENCY_VIBRATION_PATTERN, if (loop) 0 else -1)
                }
            } catch (_: Exception) {
                // Tolerate vibration failure gracefully
            }
        }
    }

    @Synchronized
    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        } catch (_: Exception) {
            mediaPlayer = null
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null && audioManager != null) {
                audioManager?.abandonAudioFocusRequest(focusRequest!!)
            }
            focusRequest = null
            audioManager = null
        } catch (_: Exception) {
            focusRequest = null
            audioManager = null
        }

        try {
            vibrator?.cancel()
            vibrator = null
        } catch (_: Exception) {
            vibrator = null
        }
    }

    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
}
