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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.mobiai.app.ultils.Announcer
import com.mobiai.app.ultils.FlashlightHelper
import com.mobiai.base.basecode.storage.SharedPreferenceUtils


class SmsBroadcastReceiver : BroadcastReceiver() {
    private var smsMessagebody: String? = ""
    private var senderName: String? = null
    private var name: String? = null
    private lateinit var audioManager: AudioManager
    private lateinit var announcer: Announcer
    private val handler = Handler(Looper.getMainLooper())


    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent) {
        audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        SharedPreferenceUtils.currentMusic =
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        SharedPreferenceUtils.currentRing = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        SharedPreferenceUtils.beforeMode = audioManager.ringerMode
        announcer = context.let { Announcer(it) }!!
        val flashlightHelper = context.let { FlashlightHelper(it) }
        val serviceIntent = Intent(context, TextToSpeechSmsService::class.java)

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
                        Log.d("SMSBroadcast", "senderName : ${senderName} va onReceivename: $name")
                        cursor?.close()
                    }

                    Log.d("SMSBroadcast", "onReceiveContent: $smsMessagebody ")
                    serviceIntent.putExtra(
                        "senderName",
                        "${senderName?.let { formatPhoneNumber(it) }}"
                    )
                    serviceIntent.putExtra("name", "$name")
                    serviceIntent.putExtra("smsMessagebody", "$smsMessagebody")
                    if (SharedPreferenceUtils.isTurnOnSms) {
                        Log.d("TestABC","flash : ${SharedPreferenceUtils.isTurnOnFlashSms}")
                        if (SharedPreferenceUtils.isTurnOnFlashSms) {
                            flashlightHelper.blinkFlash(150)
                            handler.postDelayed({
                                flashlightHelper.stopBlink()
                                flashlightHelper.stopFlash()
                            }, 1000)
                        }
                        var battery = announcer.getBatteryPercentage(context)
                        Log.d("SMSBroadcast", "battery: $battery ")
                        if ( battery >= SharedPreferenceUtils.batteryMin) {
                            if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL && SharedPreferenceUtils.isTurnOnSmsNormal)
                            {
                                if(!SharedPreferenceUtils.checkCount){
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(serviceIntent)
                                    }
                                    else{
                                        context.startService(serviceIntent)
                                    }
                                    SharedPreferenceUtils.checkCount = true
                                }
                                else
                                    ContextCompat.startForegroundService(context,serviceIntent)
                            }
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE && SharedPreferenceUtils.isTurnOnSmsVibrate)
                            {
                                if(!SharedPreferenceUtils.checkCount){
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(serviceIntent)
                                    }
                                    else{
                                        context.startService(serviceIntent)
                                    }
                                    SharedPreferenceUtils.checkCount = true
                                }
                                else
                                    ContextCompat.startForegroundService(context,serviceIntent)
                            }
                            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT && SharedPreferenceUtils.isTurnOnSmsSilent)
                            {
                                if(!SharedPreferenceUtils.checkCount){
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(serviceIntent)
                                    }
                                    else{
                                        context.startService(serviceIntent)
                                    }
                                    SharedPreferenceUtils.checkCount = true
                                }
                                else
                                    ContextCompat.startForegroundService(context,serviceIntent)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("Exception caught", "${e.message}")
            }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace("", "  ")
    }


}