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
import android.speech.tts.TextToSpeech
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.FlashlightHelper
import com.mobiai.app.ultils.tts
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.*

class MyBroadcastReceiver : BroadcastReceiver() {
    private lateinit var announcer: Announcer
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var serviceIntent: Intent


    override fun onReceive(context: Context?, intent: Intent?) {
        val audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val flashlightHelper = context.let { FlashlightHelper.getInstance(it) }
        announcer = context.let { Announcer(it) }
        if (SharedPreferenceUtils.checkMode) {
            SharedPreferenceUtils.beforeMode = audioManager.ringerMode
            val currentMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val currentRing = audioManager.getStreamVolume(AudioManager.STREAM_RING)
            SharedPreferenceUtils.currentMusic = currentMusic
            SharedPreferenceUtils.currentRing = currentRing
            SharedPreferenceUtils.checkMode = !SharedPreferenceUtils.checkMode

        }
        val phoneNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
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
                        {
                            if(!SharedPreferenceUtils.checkCount){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent)
                                }
                                else{
                                    context.startService(serviceIntent)

                                }
                                SharedPreferenceUtils.checkCount = true
                            }
                            else
                                ContextCompat.startForegroundService(context,serviceIntent)
                        }
                        else if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnModeVibrate)
                        {
                            if(!SharedPreferenceUtils.checkCount){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent)
                                }
                                else{
                                    context.startService(serviceIntent)

                                }
                                SharedPreferenceUtils.checkCount = true
                            }
                            else
                                ContextCompat.startForegroundService(context,serviceIntent)
                        }
                        else if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnModeSilent)
                        {
                            if(!SharedPreferenceUtils.checkCount){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent)
                                }
                                else{
                                    context.startService(serviceIntent)

                                }
                                SharedPreferenceUtils.checkCount = true
                            }
                            else
                                ContextCompat.startForegroundService(context,serviceIntent)
                        }
                    }
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    SharedPreferenceUtils.checkMode = true
                    if(textToSpeech != null){
                        textToSpeech?.stop()
                        handler.postDelayed({
                            textToSpeech?.stop()
                        },200)
                    }
                    audioManager.ringerMode = SharedPreferenceUtils.beforeMode
                    flashlightHelper.stopBlink()
                    flashlightHelper.stopFlash()
                    setVolume(context)
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    SharedPreferenceUtils.checkMode = true
                    if(textToSpeech != null){
                        textToSpeech?.stop()
                        handler.postDelayed({
                            textToSpeech?.stop()
                        },200)
                    }
                    audioManager.ringerMode = SharedPreferenceUtils.beforeMode
                    flashlightHelper.stopBlink()
                    flashlightHelper.stopFlash()
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
            {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING,
                    SharedPreferenceUtils.currentRing, 0)
                audioManager.ringerMode = SharedPreferenceUtils.beforeMode
            }

            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, SharedPreferenceUtils.currentMusic, 0)
        }, 500)

    }
}