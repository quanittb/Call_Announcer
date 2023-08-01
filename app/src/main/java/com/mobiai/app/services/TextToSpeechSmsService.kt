package com.mobiai.app.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import com.mobiai.R
import com.mobiai.app.ultils.ContactInfomation
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.Locale


class TextToSpeechSmsService : Service(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager
    private var volumeRing: Int = 0


    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        audioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val ratioMusic =
            (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toFloat()
        val speechVolume =
            Math.round(SharedPreferenceUtils.volumeAnnouncer.toFloat() / ratioMusic)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, speechVolume, 0
        )
        val ratioRing =
            (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)).toFloat()
        volumeRing = Math.round(SharedPreferenceUtils.volumeRing.toFloat() / ratioRing)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_VIBRATE) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING,
                    volumeRing,
                    0
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val senderName = intent?.getStringExtra("senderName")
        val name = intent?.getStringExtra("name")
        val smsMessagebody = intent?.getStringExtra("smsMessagebody")

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        textToSpeech.setSpeechRate(SharedPreferenceUtils.speedSpeak.toFloat() / 40.toFloat())
        handler.postDelayed(
            {
                if (!SharedPreferenceUtils.isReadNameSms && !SharedPreferenceUtils.isUnknownNumberSms)
                    textToSpeech.speak(
                        "${getString(R.string.there_is_a_message_with_content)} $smsMessagebody",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        "read"
                    )
                else if (SharedPreferenceUtils.isReadNameSms && name != "null")
                    textToSpeech.speak(
                        " ${getString(R.string.message_from)}  $name ${getString(R.string.with_content)} $smsMessagebody",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        "read"
                    )
                else {
                    textToSpeech.speak(
                        "${getString(R.string.message_from)} $senderName ${getString(R.string.with_content)} $smsMessagebody",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        "read"
                    )
                }

            }, 700)
        textToSpeech.setOnUtteranceCompletedListener {
            setVolume()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale(
                SharedPreferenceUtils.languageCode,
                Locale(SharedPreferenceUtils.languageCode).country.toUpperCase()
            )
        } else {
        }
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun setVolume() {
        handler.postDelayed({
            if (SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_SILENT) {
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC, SharedPreferenceUtils.currentMusic, 0
                    )
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_RING, SharedPreferenceUtils.currentRing, 0
                    )
            }
        }, 1000)
    }
}