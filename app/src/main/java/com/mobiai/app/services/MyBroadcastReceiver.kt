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
    private val handler = Handler(Looper.getMainLooper())

    override fun onReceive(context: Context?, intent: Intent?) {

        val audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val flashlightHelper = FlashlightHelper.getInstance(context)
        if (intent?.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    if(SharedPreferenceUtils.isTurnOnFlash)
                        flashlightHelper.blinkFlash(150)
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    SharedPreferenceUtils.checkMode = true
                    if(tts != null){
                        tts?.stop()
                        handler.postDelayed({
                            tts?.stop()
                        },200)
                    }
                    audioManager.ringerMode = SharedPreferenceUtils.beforeMode
                    flashlightHelper.stopBlink()
                    flashlightHelper.stopFlash()
                    setVolume(context)
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    SharedPreferenceUtils.checkMode = true
                    if(tts != null){
                        tts?.stop()
                        handler.postDelayed({
                            tts?.stop()
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