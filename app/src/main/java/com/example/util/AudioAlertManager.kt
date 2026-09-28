package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class AudioAlertManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var originalVolume: Int? = null

    init {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Triggers a loud alarm even if the device is currently in silent or vibrate mode.
     * Uses the STREAM_ALARM audio channel which by Android design is audible in silent mode.
     */
    fun startEmergencyAlarm() {
        try {
            stopEmergencyAlarm() // Ensure any prior instance is stopped

            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let { am ->
                originalVolume = am.getStreamVolume(AudioManager.STREAM_ALARM)
                val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                // Set volume to maximum for urgent override
                try {
                    am.setStreamVolume(AudioManager.STREAM_ALARM, maxVol, 0)
                } catch (e: Exception) {
                    Log.w("AudioAlertManager", "Could not set volume: ${e.message}")
                }
            }

            // Get alarm ringtone or notification sound
            var alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alertUri == null) {
                alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, alertUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }

            // Continuous pulse vibration
            vibrator?.let { vib ->
                if (vib.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val pattern = longArrayOf(0, 800, 300, 800, 300, 800)
                        val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
                        vib.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, 0))
                    } else {
                        @Suppress("DEPRECATION")
                        vib.vibrate(longArrayOf(0, 800, 300, 800), 0)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AudioAlertManager", "Failed to start emergency alarm: ${e.message}", e)
        }
    }

    fun stopEmergencyAlarm() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
            }
            mediaPlayer = null

            vibrator?.cancel()

            // Restore original volume if saved
            originalVolume?.let { orig ->
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                try {
                    audioManager?.setStreamVolume(AudioManager.STREAM_ALARM, orig, 0)
                } catch (_: Exception) {}
                originalVolume = null
            }
        } catch (e: Exception) {
            Log.e("AudioAlertManager", "Failed to stop emergency alarm: ${e.message}", e)
        }
    }
}
