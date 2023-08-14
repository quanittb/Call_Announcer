package com.mobiai.app.services

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.mobiai.R
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.ContactInfomation
import com.mobiai.app.ultils.tts
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.N)
class CallDetectorService : CallScreeningService(), TextToSpeech.OnInitListener {
    private lateinit var textToSpeech: TextToSpeech

    override fun onScreenCall(callDetails: Call.Details) {
        val handler = Handler(Looper.getMainLooper())

        val response = CallResponse.Builder()
        val phoneNumber = callDetails.handle.toString()
        val name = ContactInfomation.getContactInfo(this, phoneNumber)
        Log.d("TAG", "onScreenCall: $name")
        textToSpeech = TextToSpeech(this, this)
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        val announcer = Announcer(this)
        announcer.initTTS(this)
        val audioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if(phoneNumber.isNotEmpty()){
            if(!SharedPreferenceUtils.isUnknownNumber && name == phoneNumber) {
                if(SharedPreferenceUtils.beforeMode== AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.seekBarRing != 0)
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, SharedPreferenceUtils.volumeRing, 0)
                return
            }
            else {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, SharedPreferenceUtils.volumeAnnouncer, 0
                )
                if(SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.seekBarRing == 0){
                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                }
                if(SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.seekBarRing != 0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMinVolume(AudioManager.STREAM_RING)+1, 0)
                    }
            }
        }
        announcer.setSpeechRate(SharedPreferenceUtils.speedSpeak)

        handler.postDelayed({
            if (phoneNumber.isNotEmpty()) {
                if(name != phoneNumber && name != null){
                    if(SharedPreferenceUtils.isReadName){
                        announcer.readText(" $name ${getString(R.string.incoming_call)} ",params)
                    }
                    else
                    {
                        announcer.readText(" ${formatNumber(phoneNumber)} ${getString(R.string.incoming_call)} ",params)
                    }
                }
                else{
                    if(SharedPreferenceUtils.isUnknownNumber){
                        announcer.readText(" ${formatNumber(phoneNumber)} ${getString(R.string.incoming_call)} ",params)
                    }
                }
            }
        }, 1000)

       // params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read2")

        tts?.setOnUtteranceCompletedListener { utteranceId ->
            handler.postDelayed({
                if (utteranceId == "read") {
                    if(name != phoneNumber && name != null){
                        if(SharedPreferenceUtils.isReadName){
                            announcer.readText(" $name ${getString(R.string.incoming_call)} ",params)
                        } else{
                            announcer.readText(" ${formatNumber(phoneNumber)} ${getString(R.string.incoming_call)} ",params)
                        }
                    } else{
                        if(SharedPreferenceUtils.isUnknownNumber){
                            announcer.readText(" ${formatNumber(phoneNumber)} ${getString(R.string.incoming_call)} ",params)
                        }
                    }
                    tts?.setOnUtteranceCompletedListener { utteranceId ->
                        if (utteranceId == "read" && SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.seekBarRing != 0 ) {
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, SharedPreferenceUtils.volumeRing, 0)
                        }
                    }
                }
            },200)
        }
        respondToCall(callDetails, response.build())
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
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

    private fun formatNumber(input1: String): String {
        val stringBuilder = StringBuilder()
        val input2 = input1.substringAfter("tel:").trim()
        for (i in input2.indices) {
            stringBuilder.append(input2[i])
            if (i < input2.length - 1) {
                stringBuilder.append("  ")
            }
        }
        return stringBuilder.toString()
    }
}
