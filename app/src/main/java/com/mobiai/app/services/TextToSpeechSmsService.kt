package com.mobiai.app.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.core.app.NotificationCompat
import com.mobiai.R
import com.mobiai.app.ultils.ContactInfomation
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import java.util.Locale


class TextToSpeechSmsService : Service(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager


    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        var senderName = intent?.getStringExtra("senderName")
        var name = intent?.getStringExtra("name")
        var smsMessagebody = intent?.getStringExtra("smsMessagebody")
        Log.d("TestABC", "senderName : $senderName va name : $name va smsMessagebody: $smsMessagebody")
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,SharedPreferenceUtils.volumeAnnouncer,0)
        textToSpeech.setSpeechRate(SharedPreferenceUtils.speedSpeak.toFloat() / 40.toFloat())
        Log.d("TestABC", "SMS :${SharedPreferenceUtils.isUnknownNumberSms} ")

        handler.postDelayed(
            {
                if (SharedPreferenceUtils.isUnknownNumberSms) {
                    if(name=="null") name = senderName
                    textToSpeech.speak(" ${getString(R.string.there_is_a_message_with_content)} $name ${getString(R.string.with_content)} $smsMessagebody ", TextToSpeech.QUEUE_FLUSH, params, "read")
                }
                else {
                    if (name != "null"){
                        textToSpeech.speak(" ${getString(R.string.there_is_a_message_with_content)} $name ${getString(R.string.with_content)} $smsMessagebody ", TextToSpeech.QUEUE_FLUSH, params, "read")
                    }
                }

            }, 200
        )
        textToSpeech.setOnUtteranceCompletedListener {
            setVolume()
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
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
    private fun createNotification(): Notification {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SMS Service")
            .setContentText("Running...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "SmsSpeechServiceChannel"
    }
}