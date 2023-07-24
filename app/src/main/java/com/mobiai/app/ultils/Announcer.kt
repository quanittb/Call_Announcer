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

class Announcer(context: Context) : TextToSpeech.OnInitListener  {
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

    fun readText(text: String, params: Bundle) {
        var audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            2 ,
            0)
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, params, null)

    }

    // Hàm hiển thị dialog để người dùng chọn ngôn ngữ
    private fun showLanguageDialog(languages: List<Locale>) {
        // Tạo dialog chứa danh sách ngôn ngữ
        val dialog = AlertDialog.Builder(context)
            .setTitle("Chọn ngôn ngữ")
            .setItems(languages.map { it.displayLanguage }.toTypedArray()) { _, position ->
                // Xử lý khi người dùng chọn một ngôn ngữ
                selectedLanguage = languages[position]
                tts?.setLanguage(selectedLanguage)
            }
            .setCancelable(true)
            .create()

        // Hiển thị dialog
        dialog.show()
    }

    fun showSupportedLanguages() {
        val supportedLanguages = tts!!.availableLanguages
        // Hiển thị danh sách ngôn ngữ đã được hỗ trợ
        showLanguageDialog(supportedLanguages.toList())
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

    fun AnnouncerForMinBattery(minBattery: Float, text: String, context: Context, params: Bundle) {
        if (getBatteryPercentage(context) >= minBattery)
            readText(text,params)
    }

//    override fun onUtteranceCompleted(utteranceId: String?) {
//        isRinging = true
//        handler.postDelayed({
//            if(isRinging)
//                setVolume()
//        },500)
//
//    }
//    fun setVolume(){
//        var audioManager =
//            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
//        audioManager.setStreamVolume(
//            AudioManager.STREAM_RING,
//            SharedPreferenceUtils.currentRing ,
//            0)
//        audioManager.setStreamVolume(
//            AudioManager.STREAM_MUSIC,
//            SharedPreferenceUtils.currentMusic,
//            0
//        )
//    }
}