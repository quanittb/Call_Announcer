package com.mobiai.app.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.FlashlightHelper
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.*

class MyBroadcastReceiver : BroadcastReceiver() {
    private lateinit var announcer: Announcer
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var serviceIntent: Intent

    @RequiresApi(Build.VERSION_CODES.P)

    override fun onReceive(context: Context?, intent: Intent?) {
//        if (mediaPlayer == null) mediaPlayer =
//            MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI)
        val audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val flashlightHelper = context.let { FlashlightHelper.getInstance(it) }
        announcer = context.let { Announcer(it) }
        Log.d("beforeMode","checkmode : ${SharedPreferenceUtils.checkMode}")
        if (SharedPreferenceUtils.checkMode) {
            SharedPreferenceUtils.beforeMode = audioManager.ringerMode
            val currentMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val currentRing = audioManager.getStreamVolume(AudioManager.STREAM_RING)
            SharedPreferenceUtils.currentMusic = currentMusic
            SharedPreferenceUtils.currentRing = currentRing
            SharedPreferenceUtils.checkMode = !SharedPreferenceUtils.checkMode
            Log.d("beforeMode","checkmode lần 2 : ${SharedPreferenceUtils.checkMode}")

        }
        val phoneNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        Log.d("BeforeMode", "Phone : $phoneNumber")
        Log.d("BeforeMode", "Mode : ${SharedPreferenceUtils.beforeMode}")
        if (intent?.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            serviceIntent = Intent(context, TextToSpeechCallerService::class.java)

            val batteryPhone = context.let { announcer.getBatteryPercentage(it) }
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    serviceIntent.putExtra("textToRead", "$phoneNumber")
                    if (SharedPreferenceUtils.isTurnOnCall && batteryPhone >= SharedPreferenceUtils.batteryMin) {
                        if (SharedPreferenceUtils.isTurnOnFlash) flashlightHelper.blinkFlash(150)

                        if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnModeNormal)
                            context.startService(serviceIntent)
                        else if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnModeVibrate)
                                context.startService(serviceIntent)
                        else if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnModeSilent)
                                context.startService(serviceIntent)
                    }
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    SharedPreferenceUtils.checkMode = true
                    audioManager.ringerMode = SharedPreferenceUtils.beforeMode
                    flashlightHelper?.stopBlink()
                    flashlightHelper?.stopFlash()
                    setVolume(context)
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    SharedPreferenceUtils.checkMode = true
                    audioManager.ringerMode = SharedPreferenceUtils.beforeMode
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
            if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL)
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING,
                    SharedPreferenceUtils.currentRing, 0)
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, SharedPreferenceUtils.currentMusic, 0)
        }, 500)

    }
}