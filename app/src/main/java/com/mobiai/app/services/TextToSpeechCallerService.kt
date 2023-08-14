//package com.mobiai.app.services
//
//import android.annotation.SuppressLint
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.media.AudioManager
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.os.IBinder
//import android.os.Looper
//import android.speech.tts.TextToSpeech
//import android.util.Log
//import androidx.annotation.RequiresApi
//import com.mobiai.R
//import com.mobiai.app.ultils.ContactInfomation
//import com.mobiai.base.basecode.storage.SharedPreferenceUtils
//import java.util.*
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import androidx.core.app.NotificationCompat
//import com.mobiai.app.ultils.tts
//
//class TextToSpeechCallerService : Service(), TextToSpeech.OnInitListener {
//
//    private val handler = Handler(Looper.getMainLooper())
//    private lateinit var audioManager: AudioManager
//    override fun onCreate() {
//        super.onCreate()
//        val notification = createNotification()
//        startForeground(NOTIFICATION_ID, notification)
//        val workerThread = Thread {
//            textToSpeech = TextToSpeech(this, this)
//        }
//        workerThread.start()
//
//    }
//    @SuppressLint("SuspiciousIndentation")
//    @RequiresApi(Build.VERSION_CODES.P)
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        audioManager =
//            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        var textToRead = intent?.getStringExtra("textToRead")
//        if(textToRead=="null") textToRead = null
//        var name = ContactInfomation.getContactInfo(this, textToRead)
//        if(textToRead !=null){
//            if(!SharedPreferenceUtils.isUnknownNumber && name == textToRead ) {
//                if(SharedPreferenceUtils.beforeMode== AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.seekBarRing != 0)
//                    audioManager.setStreamVolume(AudioManager.STREAM_RING, SharedPreferenceUtils.volumeRing, 0)
//                return START_STICKY
//            }
//            else {
//                audioManager.setStreamVolume(
//                    AudioManager.STREAM_MUSIC, SharedPreferenceUtils.volumeAnnouncer, 0
//                )
//                if(SharedPreferenceUtils.beforeMode== AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.seekBarRing == 0){
//                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
//                }
//                if(SharedPreferenceUtils.beforeMode== AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.seekBarRing != 0)
//                    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMinVolume(AudioManager.STREAM_RING)+1, 0)
//            }
//        }
//
//        textToSpeech?.setSpeechRate(SharedPreferenceUtils.speedSpeak.toFloat() / 40)
//        if(SharedPreferenceUtils.speedSpeak==0)
//            textToSpeech?.setSpeechRate(0.1f)
//        var params = Bundle()
//        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
//        handler.postDelayed({
//            if (!textToRead.isNullOrEmpty()) {
//                if(name != textToRead && name != null){
//                    if(SharedPreferenceUtils.isReadName){
//                        textToSpeech?.speak(" $name ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
//                    }
//                    else
//                        textToSpeech?.speak(" ${formatNumber(textToRead)} ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
//                }
//                else{
//                    if(SharedPreferenceUtils.isUnknownNumber){
//                        textToSpeech?.speak(" ${formatNumber(textToRead)} ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
//                    }
//                }
//            }
//        }, 1000)
//
//        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read2")
//        textToSpeech?.setOnUtteranceCompletedListener {utteranceId ->
//            handler.postDelayed({
//                if (utteranceId == "read") {
//                    if(name != textToRead && name != null){
//                        if(SharedPreferenceUtils.isReadName){
//                            textToSpeech?.speak(" $name ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
//                        }
//                        else
//                            textToSpeech?.speak(" ${textToRead?.let { formatNumber(it) }} ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
//                    }
//                    else{
//                        if(SharedPreferenceUtils.isUnknownNumber){
//                            textToSpeech?.speak(" ${textToRead?.let { formatNumber(it) }} ${getString(R.string.incoming_call)} ", TextToSpeech.QUEUE_FLUSH, params, "read")
//                        }
//                    }
//                    textToSpeech?.setOnUtteranceCompletedListener {utteranceId ->
//                        if (utteranceId == "read" && SharedPreferenceUtils.beforeMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.seekBarRing != 0 ) {
//                            audioManager.setStreamVolume(AudioManager.STREAM_RING, SharedPreferenceUtils.volumeRing, 0)
//                        }
//                    }
//                }
//            },200)
//        }
//        return START_STICKY
//    }
//
//    override fun onInit(status: Int) {
//        if (status == TextToSpeech.SUCCESS) {
//            textToSpeech?.language = Locale(
//                SharedPreferenceUtils.languageCode,
//                Locale(SharedPreferenceUtils.languageCode).country.toUpperCase()
//            )
//        } else {
//            textToSpeech?.language = Locale.ENGLISH
//        }
//    }
//
//    override fun onDestroy() {
//        if (textToSpeech != null) {
//            textToSpeech?.stop()
//            textToSpeech?.shutdown()
//        }
//        super.onDestroy()
//    }
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//    fun formatNumber(input: String): String {
//        val stringBuilder = StringBuilder()
//
//        for (i in 0 until input.length) {
//            stringBuilder.append(input[i])
//            if (i < input.length - 1) {
//                stringBuilder.append("  ")
//            }
//        }
//        return stringBuilder.toString()
//    }
//    private fun createNotification(): Notification {
//        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Calling Service")
//            .setContentText("Running...")
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setAutoCancel(true)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "Foreground Service Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        return notificationBuilder.build()
//    }
//
//    companion object {
//        const val NOTIFICATION_ID = 1
//        const val CHANNEL_ID = "CallSpeechServiceChannel"
//    }
//}
//var textToSpeech: TextToSpeech? = null
//
