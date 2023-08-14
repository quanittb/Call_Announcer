package com.mobiai.app.ultils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log

class ContactInfomation {
    companion object{
        fun getContactInfo(context: Context,phoneNumber: String?): String? {
            if (phoneNumber == "") {
                return ""
            }
            val uri: Uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )

            context.contentResolver.query(
                uri,
                arrayOf(
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_URI
                ),
                null, null, null
            )
                ?.use {
                    if (it.moveToFirst()) {
                        val displayName = it.getString(0)
                        Log.i("TAG", "getContactInfo: $displayName")
                        return displayName
                    }
                }
            return phoneNumber
        }

        @SuppressLint("Range")
        fun getPhoneNumberFromContacts(context: Context, name: String): String? {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                arrayOf(name),
                null
            )

            var phoneNumber: String? = null
            if (cursor != null && cursor.moveToFirst()) {
                phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                cursor.close()
            }

            return phoneNumber
        }

    }
}