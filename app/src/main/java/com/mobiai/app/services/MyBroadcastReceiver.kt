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
    private var check = 1
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        val flashlightHelper = context?.let { FlashlightHelper.getInstance(it) }
        val audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        announcer = context.let { Announcer(it) }
        val currentMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val currentRing = audioManager.getStreamVolume(AudioManager.STREAM_RING)

        if(check==1) SharedPreferenceUtils.beforeMode = audioManager.ringerMode
        check ++
        Log.d("ABCDE","Check : $check")
        Log.d("ABCDE","before mode : ${SharedPreferenceUtils.beforeMode}")
        SharedPreferenceUtils.currentMusic = currentMusic
        SharedPreferenceUtils.currentRing = currentRing
        if (intent?.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            serviceIntent = Intent(context, TextToSpeechCallerService::class.java)
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            serviceIntent.putExtra("textToRead", "$phoneNumber")
            Log.d("ABCDE","Phone : $phoneNumber")
            // Kiểm tra trạng thái cuộc gọi
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    Log.d("ABCDE","chế độ ban đầu : ${SharedPreferenceUtils.beforeMode}")
                    // Lấy số điện thoại từ intent
                    if (SharedPreferenceUtils.isTurnOnCall && context?.let { announcer.getBatteryPercentage(it) }!! >= SharedPreferenceUtils.batteryMin) {
                        if (SharedPreferenceUtils.isTurnOnFlash) flashlightHelper?.blinkFlash(150)
                        if ( SharedPreferenceUtils.isUnknownNumber || SharedPreferenceUtils.isReadName) {
                            if(SharedPreferenceUtils.seekBarRing == 0) audioManager.setStreamVolume(AudioManager.STREAM_RING,0,0)
                            if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnModeNormal)
                            {

                                //if(SharedPreferenceUtils.seekBarRing == 0) audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                                context.startService(serviceIntent)


                            }

                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnModeVibrate) context.startService(serviceIntent)
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnModeSilent) context.startService(serviceIntent)
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
        audioManager.ringerMode = SharedPreferenceUtils.beforeMode
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
