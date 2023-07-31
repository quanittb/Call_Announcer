package com.mobiai.app.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.FlashlightHelper
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.*

class MyBroadcastReceiver : BroadcastReceiver() {
    //private lateinit var audioManager: AudioManager
    private lateinit var announcer: Announcer
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var serviceIntent : Intent
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        val flashlightHelper = context?.let { FlashlightHelper.getInstance(it) }
        val audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val currentRing = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        SharedPreferenceUtils.beforeMode = audioManager.ringerMode
        SharedPreferenceUtils.currentMusic = currentMusic
        SharedPreferenceUtils.currentRing = currentRing
        Log.d("ABCDE","Mode: ${SharedPreferenceUtils.beforeMode} +   ${SharedPreferenceUtils.currentMusic} +   ${SharedPreferenceUtils.volumeAnnouncer} +   ${SharedPreferenceUtils.volumeRing}")
        if (intent?.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            // Kiểm tra trạng thái cuộc gọi
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    serviceIntent = Intent(context, TextToSpeechCallerService::class.java)
                    val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    serviceIntent.putExtra("textToRead", "$phoneNumber")

                    // Lấy số điện thoại từ intent
                    if (SharedPreferenceUtils.isTurnOnCall) {
                        if (SharedPreferenceUtils.isTurnOnFlash) flashlightHelper?.blinkFlash(
                            150
                        )
                        announcer = context.let { Announcer(it) }
                        if (context?.let { announcer.getBatteryPercentage(it) }!! >= SharedPreferenceUtils.batteryMin && (SharedPreferenceUtils.isUnknownNumber || SharedPreferenceUtils.isReadName)) {

                            if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnModeNormal) context.startService(
                                serviceIntent
                            )
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnModeVibrate) context.startService(
                                serviceIntent
                            )
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnModeSilent) context.startService(
                                serviceIntent
                            )
                        }

                    }
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {
                    flashlightHelper?.stopBlink()
                    flashlightHelper?.stopFlash()
                    //context?.stopService(serviceIntent)
                    setVolume(context)

                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    //context?.stopService(serviceIntent)
                    flashlightHelper?.stopBlink()
                    flashlightHelper?.stopFlash()
                    setVolume(context)

                }
            }

        }
    }

    fun setVolume(context: Context?) {
        val audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        handler.postDelayed({
            if (SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_SILENT)
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING, SharedPreferenceUtils.currentRing, 0
                )
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, SharedPreferenceUtils.currentMusic, 0
            )
        }, 1000)

    }

}
