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
import java.util.*

class TextToSpeechCallerService : Service(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager
    private var volumeRing: Int = 0


    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        audioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
        Log.d("ABCDE","volume ring : ${SharedPreferenceUtils.volumeAnnouncer} va $ratioMusic va audio hien tai : ${audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)}")
        textToSpeech.setSpeechRate(SharedPreferenceUtils.speedSpeak.toFloat() / 40.toFloat())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_VIBRATE) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING,
                    1,
                    0
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_VIBRATE) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING,
                    audioManager.getStreamMinVolume(AudioManager.STREAM_RING),
                    0
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var textToRead = intent?.getStringExtra("textToRead")
        if(textToRead=="null") textToRead = null
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        handler.postDelayed({
            if (!textToRead.isNullOrEmpty()) {
                if (SharedPreferenceUtils.isReadName || SharedPreferenceUtils.isUnknownNumber) {
                    var name = ContactInfomation.getContactInfo(this, textToRead)
                    Log.d("TestABC", "name: $name va texttoread: $textToRead")
                    if (name == textToRead) name = formatNumber(name)
                    textToSpeech.speak(
                        " $name ${getString(R.string.incoming_call)} ",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        "read"
                    )
                }
                if (!SharedPreferenceUtils.isReadName && SharedPreferenceUtils.isUnknownNumber) {
                    textToSpeech.speak(
                        " ${formatNumber(textToRead)} ${getString(R.string.incoming_call)} ",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        "read"
                    )
                }
            } else {
                textToSpeech.speak(
                    " ${getString(R.string.you_have_an_incoming_call)} ",
                    TextToSpeech.QUEUE_FLUSH,
                    params,
                    "read"
                )
            }
        }, 500)
        Log.d(
            "TestABC",
            "Chưa chạy hàm đọc xong Before mode: ${SharedPreferenceUtils.beforeMode} va ${AudioManager.RINGER_MODE_NORMAL}"
        )
        textToSpeech.setOnUtteranceCompletedListener {
            Log.d(
                "TestABC",
                "Đã đọc xong Before mode: ${SharedPreferenceUtils.beforeMode} va ${AudioManager.RINGER_MODE_NORMAL}"
            )
            if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL) {
                Log.d("TestABC", "Volume Ring của app lúc tts : $volumeRing")
                handler.postDelayed({
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.beforeMode != AudioManager.RINGER_MODE_VIBRATE) {
                            audioManager.setStreamVolume(
                                AudioManager.STREAM_RING,
                                volumeRing,
                                0
                            )
                        }
                    }
                    Log.d(
                        "TestABC",
                        "Volume Ring của máy lúc tts : ${audioManager.getStreamVolume(AudioManager.STREAM_RING)}"
                    )
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }, 800)
            }

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

    fun formatNumber(input: String): String {
        val stringBuilder = StringBuilder()

        for (i in 0 until input.length) {
            stringBuilder.append(input[i])
            if (i < input.length - 1) {
                stringBuilder.append("  ")
            }
        }
        Log.e("Number", "number $stringBuilder")
        return stringBuilder.toString()
    }
}
