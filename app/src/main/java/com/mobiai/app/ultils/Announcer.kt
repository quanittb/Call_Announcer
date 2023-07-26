package com.mobiai.app.ultils

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.text.PrecomputedText.Params
import android.widget.Toast
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.Locale

class Announcer(context: Context) : TextToSpeech.OnInitListener {
    var tts: TextToSpeech? = null
    var context = context
    private var selectedLanguage: Locale = Locale.getDefault()
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(selectedLanguage)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                openTextToSpeechSettings(this.context)
            }
        }
    }

    fun initTTS(context: Context) {
        tts = TextToSpeech(context, this)
    }

    fun readText(text: String, params: Bundle?) {
        var audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            2,
            0
        )
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, params, "read")

    }

    fun openTextToSpeechSettings(context: Context) {
        val intent = Intent()
        intent.action = "com.android.settings.TTS_SETTINGS"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Cần cài đặt ngôn ngữ nói !", Toast.LENGTH_LONG).show()
        }
    }

    fun getBatteryPercentage(context: Context): Float {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryPercentage: Float
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val batteryCapacity =
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            batteryPercentage = batteryCapacity.toFloat()
        } else {
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, intentFilter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

            batteryPercentage = level * 100 / scale.toFloat()
        }
        return batteryPercentage
    }
}