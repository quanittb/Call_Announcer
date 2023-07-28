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
import com.mobiai.R
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.ContactInfomation
import com.mobiai.app.ultils.FlashlightHelper
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.*
import kotlin.properties.Delegates

class TextToSpeechService : Service(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager
    private var volumeRing: Int = 0

    private var beforeMode by Delegates.notNull<Int>()

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        audioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        beforeMode = audioManager.ringerMode

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
        textToSpeech.setSpeechRate(SharedPreferenceUtils.speedSpeak.toFloat() / 40.toFloat())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val textToRead = intent?.getStringExtra("textToRead")
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        handler.postDelayed({
            if (!textToRead.isNullOrEmpty()) {
                if (SharedPreferenceUtils.isReadName || SharedPreferenceUtils.isUnknownNumber) {
                    var name = ContactInfomation.getContactInfo(this, textToRead)
                    if(name == textToRead) name = fomartNumber(name)
                    textToSpeech.speak(
                        " $name ${getString(R.string.incoming_call)} ",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        null
                    )
                }
                if (!SharedPreferenceUtils.isReadName && SharedPreferenceUtils.isUnknownNumber) {
                    textToSpeech.speak(
                        " $textToRead ${getString(R.string.incoming_call)} ",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        null
                    )
                }
            }
            else{
                textToSpeech.speak(
                    " ${getString(R.string.you_have_an_incoming_call)} ",
                    TextToSpeech.QUEUE_FLUSH,
                    params,
                    null
                )
            }
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
            }, 500)
        }, 200)

        textToSpeech.setOnUtteranceCompletedListener {
            if (beforeMode == AudioManager.RINGER_MODE_NORMAL) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING, volumeRing, 0
                )
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.getDefault()
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

    fun fomartNumber(input: String): String {
        val stringBuilder = StringBuilder()

        for (i in 0 until input.length) {
            stringBuilder.append(input[i])
            if (i < input.length - 1) {
                stringBuilder.append(" ")
            }
        }

        return stringBuilder.toString()
    }
}
