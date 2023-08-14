package com.mobiai.app.services

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.util.Log
import com.mobiai.R
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.Locale

class MyNotificationListenerService : NotificationListenerService(), TextToSpeech.OnInitListener {
    private lateinit var textToSpeech: TextToSpeech
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        textToSpeech = TextToSpeech(this, this)
        audioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL ) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING,
                    SharedPreferenceUtils.volumeRing,
                    0
                )
            }
        }
        val title = sbn.notification.extras.getString("android.title")
        val message = sbn.notification.extras.getString("android.text")

        if (sbn.packageName == "com.google.android.apps.messaging") {
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                SharedPreferenceUtils.volumeAnnouncer,0)
            textToSpeech.setSpeechRate(SharedPreferenceUtils.speedSpeak.toFloat() / 40.toFloat())
            if(SharedPreferenceUtils.speedSpeak==0)
                textToSpeech.setSpeechRate(0.1f)
            if(SharedPreferenceUtils.checkCountSms){
                handler.postDelayed(
                    {
                        if (!title.isNullOrEmpty()) {
                            if(containsLetters(title.toString())){
                                if(SharedPreferenceUtils.isReadNameSms){
                                    textToSpeech.speak(" ${getString(R.string.there_is_a_message_with_content)} $title ${getString(
                                        R.string.with_content)} $message ", TextToSpeech.QUEUE_FLUSH, params, "read")
                                }
                                if(!SharedPreferenceUtils.isReadNameSms)
                                    textToSpeech.speak(" ${getString(R.string.there_is_a_message_with_content)} $title ${getString(
                                        R.string.with_content)} $message ", TextToSpeech.QUEUE_FLUSH, params, "read")
                            }

                            else{
                                if(SharedPreferenceUtils.isUnknownNumberSms){
                                    textToSpeech.speak(" ${getString(R.string.there_is_a_message_with_content)} $title ${getString(
                                        R.string.with_content)} $message ", TextToSpeech.QUEUE_FLUSH, params, "read")

                                }
                            }
                        }

                    }, 200
                )
                textToSpeech.setOnUtteranceCompletedListener {
                    setVolume()
                }
            }
            else
                SharedPreferenceUtils.checkCountSms = true

        }
    }
    fun containsLetters(input: String): Boolean {
        val letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return input.any { it in letters }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale(
                SharedPreferenceUtils.languageCode,
                Locale(SharedPreferenceUtils.languageCode).country.toUpperCase()
            )
        } else {
            textToSpeech.language = Locale.getDefault()
        }
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
    fun setVolume() {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, SharedPreferenceUtils.currentMusic, 0
        )
        handler.postDelayed({
            if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING, SharedPreferenceUtils.currentRing, 0
                )
            }
        }, 1000)
    }
}
