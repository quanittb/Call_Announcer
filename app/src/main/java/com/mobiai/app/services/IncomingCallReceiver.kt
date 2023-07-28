package com.mobiai.app.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.telephony.TelephonyManager
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.FlashlightHelper
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import kotlin.properties.Delegates

class IncomingCallReceiver : BroadcastReceiver() {
    private lateinit var audioManager: AudioManager
    private lateinit var announcer: Announcer
    private val handler = Handler(Looper.getMainLooper())
    private var volumeRing: Int = 0

    private var beforeMode by Delegates.notNull<Int>()

    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent?) {
        audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        beforeMode = audioManager.ringerMode
        announcer = context?.let { Announcer(it) }!!
        announcer?.initTTS(context)
        val flashlightHelper = context?.let { FlashlightHelper.getInstance(it) }
        SharedPreferenceUtils.currentMusic =
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        SharedPreferenceUtils.currentRing =
            audioManager.getStreamVolume(AudioManager.STREAM_RING)
        var ratioMusic =
            (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toFloat()
        var speechVolume =
            Math.round(SharedPreferenceUtils.volumeAnnouncer.toFloat() / ratioMusic)

        var ratioRing =
            (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)).toFloat()
        volumeRing = Math.round(SharedPreferenceUtils.volumeRing.toFloat() / ratioRing)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, speechVolume, 0
        )
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            audioManager.setStreamVolume(
                AudioManager.STREAM_RING, volumeRing, 0
            )
        }
        val formattedSpeechNumber =
            SharedPreferenceUtils.speedSpeak.toFloat() / 20.toFloat()
        announcer.tts?.setSpeechRate(formattedSpeechNumber)
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val extras = intent.extras
            if (extras != null) {
                val state = extras.getString(TelephonyManager.EXTRA_STATE)
                when (state) {
                    TelephonyManager.EXTRA_STATE_RINGING -> {
                        if (SharedPreferenceUtils.isTurnOnCall) {
                            if (SharedPreferenceUtils.isTurnOnFlash) flashlightHelper?.blinkFlash(
                                150
                            )
                            if (announcer.getBatteryPercentage(context) >= SharedPreferenceUtils.batteryMin) {
                                if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnModeNormal) readText()
                                else if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnModeVibrate) readText()
                                else if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnModeSilent) readText()
                            }

                        }
                    }

                    TelephonyManager.EXTRA_STATE_IDLE -> {
                        setVolume()
                        flashlightHelper?.stopBlink()
                        flashlightHelper?.stopFlash()
                        announcer.tts?.stop()
                        announcer.tts?.shutdown()

                    }
                    TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                        setVolume()
                        flashlightHelper?.stopBlink()
                        flashlightHelper?.stopFlash()
                        announcer.tts?.stop()
                        announcer.tts?.shutdown()
                    }
                }
            }
        }
    }

    fun readText() {

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        handler.postDelayed(
            {
                announcer.tts?.speak(
                    "Đang gọi từ ABC", TextToSpeech.QUEUE_FLUSH, params, "read"
                )
                handler.postDelayed({
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (beforeMode != AudioManager.RINGER_MODE_SILENT && beforeMode != AudioManager.RINGER_MODE_VIBRATE) {
                            audioManager.setStreamVolume(
                                AudioManager.STREAM_RING,
                                audioManager.getStreamMinVolume(AudioManager.STREAM_RING),
                                0
                            )
                        }
                    }
                }, 400)

            }, 100
        )
        announcer.tts?.setOnUtteranceCompletedListener {
            if (beforeMode == AudioManager.RINGER_MODE_NORMAL) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING, volumeRing, 0
                )
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
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