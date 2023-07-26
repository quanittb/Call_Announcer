package com.mobiai.app.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.mobiai.app.ultils.Announcer


class SmsBroadcastReceiver : BroadcastReceiver() {
    private var smsMessagebody:String?=null
    private var senderName:String?=null
    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            val bundle = intent.extras
            try {
                if (bundle != null) {
                    val pdus =
                        bundle["pdus"] as Array<*>?
                    for (i in pdus!!.indices) {
                        val smsMessage: SmsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) SmsMessage.createFromPdu(
                            pdus[i] as ByteArray, bundle.getString("format")
                        ) else SmsMessage.createFromPdu(
                            pdus[i] as ByteArray
                        )
                        //get message
                        smsMessagebody = smsMessage.messageBody

                        senderName = smsMessage.originatingAddress
                        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderName))
                        val cursor = context?.contentResolver?.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
                       //get name
                        val name = if (cursor?.moveToFirst() == true) {
                            cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                        } else {
                            senderName
                        }
                        Log.d("TAG", "onReceivename: $name")
                        cursor?.close()
                    }
                    Log.d("TAG", "onReceive: $smsMessagebody ")
                  /*  val anc = context?.let { Announcer(it) }
                    if (context != null) {
                        anc?.initTTS(context)
                    }
                    smsMessagebody?.let { anc?.tts?.speak(smsMessagebody,TextToSpeech.QUEUE_FLUSH,null,null) }*/
                }
            } catch (e: Exception) {
                Log.d("Exception caught", e.message!!)
            }
        }
    }
}