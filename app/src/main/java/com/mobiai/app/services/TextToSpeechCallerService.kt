package com.mobiai.app.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RequiresApi
import com.mobiai.R
import com.mobiai.app.ultils.ContactInfomation
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.*

class TextToSpeechCallerService : Service(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager
    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        audioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, SharedPreferenceUtils.volumeAnnouncer, 0
        )
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        audioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, SharedPreferenceUtils.volumeAnnouncer, 0
        )
        Log.d("BeforeMode","volume announcer : ${SharedPreferenceUtils.volumeAnnouncer}")
        textToSpeech.setSpeechRate(SharedPreferenceUtils.speedSpeak.toFloat() / 40)
        var textToRead = intent?.getStringExtra("textToRead")
        if(textToRead=="null") textToRead = null
        Log.d("BeforeMode","phone text : ${textToRead}")

        var params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        handler.postDelayed({
            if (!textToRead.isNullOrEmpty()) {
                if (SharedPreferenceUtils.isUnknownNumber) {
                    if(SharedPreferenceUtils.beforeMode== AudioManager.RINGER_MODE_NORMAL)
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMinVolume(AudioManager.STREAM_RING)+1, 0)
                    var name = ContactInfomation.getContactInfo(this, textToRead)
                    Log.d("ABCDE","Name : $name va text : $textToRead")
                    if (name == textToRead)
                        textToSpeech.speak(" ${formatNumber(textToRead)} ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
                    else
                        textToSpeech.speak(" $name ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")

                }
                else {
                    var name = ContactInfomation.getContactInfo(this, textToRead)
                    Log.d("ABCDE","Name : $name va text : $textToRead")
                    if (name != textToRead){
                        if(SharedPreferenceUtils.beforeMode== AudioManager.RINGER_MODE_NORMAL)
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMinVolume(AudioManager.STREAM_RING)+1, 0)
                        textToSpeech.speak(" $name ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
                    }
                }
            }
        }, 200)

        val param2 = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read2")
        textToSpeech.setOnUtteranceCompletedListener {utteranceId ->
            if (utteranceId == "read") {
                if (SharedPreferenceUtils.isUnknownNumber) {
                    var name = ContactInfomation.getContactInfo(this, textToRead)
                    if (name == textToRead)
                        textToSpeech.speak(" ${textToRead?.let { formatNumber(it) }} ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
                    else
                        textToSpeech.speak(" $name ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, param2, "read")
                }
                else {
                    var name = ContactInfomation.getContactInfo(this, textToRead)
                    if (name != textToRead){
                        textToSpeech.speak(" $name ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, param2, "read")
                    }
                }
                textToSpeech.setOnUtteranceCompletedListener {utteranceId ->
                    if (utteranceId == "read") {
                        if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL) {
                            if (SharedPreferenceUtils.seekBarRing != 0) {
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_RING,
                                    SharedPreferenceUtils.volumeRing,
                                    0
                                )
                            }
                        }
                    }
                }
            }
        }
        return START_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale(
                SharedPreferenceUtils.languageCode,
                Locale(SharedPreferenceUtils.languageCode).country.toUpperCase()
            )
        } else {
            textToSpeech.language = Locale.ENGLISH
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
    fun formatNumber(input: String): String {
        val stringBuilder = StringBuilder()

        for (i in 0 until input.length) {
            stringBuilder.append(input[i])
            if (i < input.length - 1) {
                stringBuilder.append("  ")
            }
        }
        return stringBuilder.toString()
    }
}

