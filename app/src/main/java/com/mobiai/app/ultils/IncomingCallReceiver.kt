package com.mobiai.app.ultils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.mobiai.app.ui.fragment.CallAnnouncerFragment
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.text.DecimalFormat

class IncomingCallReceiver : BroadcastReceiver() {
    private lateinit var audioManager: AudioManager
    private lateinit var announcer: Announcer
    private val handler = Handler(Looper.getMainLooper())
    var isRinging = false
    override fun onReceive(context: Context?, intent: Intent?) {
        audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        announcer = context?.let { Announcer(it) }!!
        announcer?.initTTS(context)
        val flashlightHelper = context?.let { FlashlightHelper.getInstance(it) }

        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            SharedPreferenceUtils.currentMusic =
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            SharedPreferenceUtils.currentRing =
                audioManager.getStreamVolume(AudioManager.STREAM_RING)
            var ratio =
                (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toFloat()
            var speechVolume =
                Math.round(SharedPreferenceUtils.volumeAnnouncer.toFloat() / ratio)

            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                speechVolume,
                0
            )
            val decimalFormat = DecimalFormat("#.#")
            val formattedSpeechNumber =
                decimalFormat.format(SharedPreferenceUtils.speedSpeak.toFloat() / 20)
            if (formattedSpeechNumber.toFloat() == 0f) announcer.tts?.setSpeechRate(0.1f)
            else announcer.tts?.setSpeechRate(formattedSpeechNumber.toFloat())
            val extras = intent.extras
            if (extras != null) {
                val state = extras.getString(TelephonyManager.EXTRA_STATE)
                when (state) {
                    TelephonyManager.EXTRA_STATE_RINGING -> {
                        if (SharedPreferenceUtils.isTurnOnCall) {
                            if (SharedPreferenceUtils.isTurnOnFlash) flashlightHelper?.blinkFlash(
                                150
                            )
                            if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnModeNormal) readText()
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnModeVibrate) readText()
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnModeSilent) readText()
                        }
                    }

                    TelephonyManager.EXTRA_STATE_IDLE -> {
                        isRinging = false
                        flashlightHelper?.stopBlink()
                        flashlightHelper?.stopFlash()
                        announcer.tts?.stop()
                        announcer.tts?.shutdown()

                    }

                    TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                        isRinging = false
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            audioManager.setStreamVolume(
                AudioManager.STREAM_RING,
                audioManager.getStreamMinVolume(AudioManager.STREAM_RING) + 1,
                0
            )
        }
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        handler.postDelayed(
            {
                announcer.tts?.speak(
                    "Đang gọi từ : 0123456789",
                    TextToSpeech.QUEUE_FLUSH,
                    params,
                    "read"
                )
            }, 100
        )
        isRinging = true
        announcer.tts?.setOnUtteranceCompletedListener { utteranceId ->
            if (utteranceId == "read") {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                handler.postDelayed({
                    startRinging()
                }, 2000)
            }
        }

    }

    fun startRinging() {

        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            SharedPreferenceUtils.currentRing,
            0
        )
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            SharedPreferenceUtils.currentMusic,
            0
        )
    }

}