package com.mobiai.app.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.mobiai.app.ui.activity.MainActivity
import com.mobiai.app.ui.fragment.HomeFragment
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.FlashlightHelper
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import kotlin.properties.Delegates


class SmsBroadcastReceiver : BroadcastReceiver() {
    private var smsMessagebody: String? = ""
    private var senderName: String? = null
    private var name: String? = null
    private lateinit var audioManager: AudioManager
    private lateinit var announcer: Announcer
    private val handler = Handler(Looper.getMainLooper())
    private var volumeRing: Int = 0

    private var beforeMode by Delegates.notNull<Int>()

    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent) {
        announcer = context?.let { Announcer(it) }!!
//        announcer?.initTTS(context)
        audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        beforeMode = audioManager.ringerMode

        val flashlightHelper = context?.let { FlashlightHelper.getInstance(it) }
        SharedPreferenceUtils.currentMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        SharedPreferenceUtils.currentRing = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        var ratioMusic =
            (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toFloat()
        var speechVolume = Math.round(SharedPreferenceUtils.volumeAnnouncer.toFloat() / ratioMusic)

        var ratioRing = (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)).toFloat()
        volumeRing = Math.round(SharedPreferenceUtils.volumeRing.toFloat() / ratioRing)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, speechVolume, 0
        )
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            audioManager.setStreamVolume(
                AudioManager.STREAM_RING, volumeRing, 0
            )
        }
        val formattedSpeechNumber = SharedPreferenceUtils.speedSpeak.toFloat() / 20.toFloat()
        announcer.tts?.setSpeechRate(formattedSpeechNumber)

        if (intent.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            val bundle = intent.extras
            try {
                if (bundle != null) {
                    val pdus = bundle["pdus"] as Array<*>?
                    for (i in pdus!!.indices) {
                        val smsMessage: SmsMessage =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) SmsMessage.createFromPdu(
                                pdus[i] as ByteArray, bundle.getString("format")
                            ) else SmsMessage.createFromPdu(
                                pdus[i] as ByteArray
                            )
                        //get message
                        smsMessagebody += smsMessage.messageBody

                        senderName = smsMessage.originatingAddress
                        val uri = Uri.withAppendedPath(
                            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderName)
                        )
                        val cursor = context?.contentResolver?.query(
                            uri,
                            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
                            null,
                            null,
                            null
                        )
                        //get name
                        name = if (cursor?.moveToFirst() == true) {
                            cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                        } else {
                            null
                        }
                        Log.d("SMSBroadcast", "onReceivename: $name")
                        cursor?.close()
                    }
                    Log.d("SMSBroadcast", "onReceiveContent: $smsMessagebody ")

                    if (SharedPreferenceUtils.isTurnOnSms) {
                        if (SharedPreferenceUtils.isTurnOnFlashSms) {
                            flashlightHelper?.blinkFlash(
                                150
                            )
                            handler.postDelayed({
                                flashlightHelper?.stopBlink()
                                flashlightHelper?.stopFlash()
                            }, 500)
                        }
                        if (announcer.getBatteryPercentage(context!!) >= SharedPreferenceUtils.batteryMin) {
                            if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnSmsNormal) readText()
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnSmsVibrate) readText()
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnSmsSilent) readText()
                        }

                    }
                }
            } catch (e: Exception) {
                Log.d("Exception caught","${e.message}")
            }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace("", "  ")
    }

    fun readText() {

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "read")
        handler.postDelayed(
            {
                if (!SharedPreferenceUtils.isReadNameSms && !SharedPreferenceUtils.isUnknownNumberSms)
                    announcer.tts?.speak(
                        "Có tin nhắn với nội dung $smsMessagebody",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        "read"
                    )
                else if (SharedPreferenceUtils.isReadNameSms && name != null)
                    announcer.tts?.speak(
                        "Có tin nhắn từ  $name với nội dung $smsMessagebody",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        "read"
                    )
                else {
                    var formattedNumber = formatPhoneNumber(senderName.toString())
                    announcer.tts?.speak(
                        "Có tin nhắn từ số điện thoại $formattedNumber với nội dung $smsMessagebody",
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        "read"
                    )
                }
                Log.e("SMSBroadcast", "đã xong")
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
                }, 1000)

            }, 200
        )
        announcer.tts?.setOnUtteranceCompletedListener {
            setVolume()
        }

    }

    fun setVolume() {
        handler.postDelayed({
            audioManager.ringerMode = beforeMode
            if (beforeMode != AudioManager.RINGER_MODE_VIBRATE && beforeMode != AudioManager.RINGER_MODE_SILENT) audioManager.setStreamVolume(
                AudioManager.STREAM_RING, SharedPreferenceUtils.currentRing, 0
            )
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, SharedPreferenceUtils.currentMusic, 0
            )
        }, 1000)

    }
}