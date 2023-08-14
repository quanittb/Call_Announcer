package com.mobiai.app.services

import android.content.Context
import android.media.AudioManager
import android.os.BatteryManager
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
class CallDetectorService : CallScreeningService() {
    private lateinit var audioManager : AudioManager
    private lateinit var announcer : Announcer
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var name : String
    override fun onScreenCall(callDetails: Call.Details) {
        Log.d("ABCDEFG","Chayj vao call")

        val response = CallResponse.Builder()
        val phoneNumber = callDetails.handle.toString()
        name = ContactInfomation.getContactInfo(this, phoneNumber).toString()
        Log.d("ABCDEFG",  "$phoneNumber onScreenCall: $name")

        announcer = Announcer(this)
        announcer.initTTS(this)
        audioManager = applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        SharedPreferenceUtils.beforeMode = audioManager.ringerMode
        val currentMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val currentRing = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        SharedPreferenceUtils.currentMusic = currentMusic
        SharedPreferenceUtils.currentRing = currentRing

        Thread{
            if(phoneNumber.isNotEmpty()){
                val batteryPhone = announcer.getBatteryPercentage(this)
                if (SharedPreferenceUtils.isTurnOnCall &&  batteryPhone >= (SharedPreferenceUtils.batteryMin)) {
                    if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnModeNormal)
                    {
                        readAnnounce(phoneNumber)
                    }
                    else if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnModeVibrate)
                    {
                        readAnnounce(phoneNumber)
                    }
                    else if (SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnModeSilent)
                    {
                        readAnnounce(phoneNumber)
                    }
                }
            }
        }.start()
        respondToCall(callDetails, response.build())
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
    private fun readAnnounce(phoneNumber: String){
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
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
        announcer.setSpeechRate(SharedPreferenceUtils.speedSpeak)

        handler.postDelayed({
            if (phoneNumber.isNotEmpty()) {
                if(name != phoneNumber ){
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
        }, 600)


    tts?.setOnUtteranceCompletedListener { utteranceId ->
        if (utteranceId == "read") {
            if(name != phoneNumber ){
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
        }
    }
}
