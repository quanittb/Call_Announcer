package com.mobiai.app.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.telephony.TelephonyManager
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.FlashlightHelper
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.*
import kotlin.properties.Delegates

class MyBroadcastReceiver : BroadcastReceiver() {
    private lateinit var audioManager: AudioManager
    private lateinit var announcer: Announcer
    private val handler = Handler(Looper.getMainLooper())
    private var volumeRing: Int = 0
    private var beforeMode by Delegates.notNull<Int>()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.PHONE_STATE") {
            announcer = context?.let { Announcer(it) }!!
            audioManager =
                context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            beforeMode = audioManager.ringerMode
            val flashlightHelper = context?.let { FlashlightHelper(it) }

            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val serviceIntent = Intent(context, TextToSpeechService::class.java)
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            serviceIntent.putExtra("textToRead", "$phoneNumber")
            // Kiểm tra trạng thái cuộc gọi
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    // Lấy số điện thoại từ intent
                    if (SharedPreferenceUtils.isTurnOnCall) {
                        if (SharedPreferenceUtils.isTurnOnFlash) flashlightHelper?.blinkFlash(
                            150
                        )
                        if (context?.let { announcer.getBatteryPercentage(it) }!! >= SharedPreferenceUtils.batteryMin && (SharedPreferenceUtils.isUnknownNumber || SharedPreferenceUtils.isReadName ) ) {
                            if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnModeNormal) context?.startService(serviceIntent)
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnModeVibrate) context?.startService(serviceIntent)
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnModeSilent) context?.startService(serviceIntent)
                        }

                    }
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    context?.stopService(serviceIntent)
                    setVolume()
                    flashlightHelper?.stopBlink()
                    flashlightHelper?.stopFlash()
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    context?.stopService(serviceIntent)
                    setVolume()
                    flashlightHelper?.stopBlink()
                    flashlightHelper?.stopFlash()
                }
            }

        }
    }

    fun setVolume() {
        handler.postDelayed({
            audioManager.ringerMode = beforeMode
            if (beforeMode != AudioManager.RINGER_MODE_VIBRATE && beforeMode != AudioManager.RINGER_MODE_SILENT)
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING, SharedPreferenceUtils.currentRing, 0
                )
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, SharedPreferenceUtils.currentMusic, 0
            )
        }, 1000)

    }

}
