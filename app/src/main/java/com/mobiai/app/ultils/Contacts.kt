package com.mobiai.app.ultils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import android.util.Log

data class Contacts(val id: String, val displayName: String, val phones: List<PhoneNumber>  = arrayListOf(), val isStar: Boolean = false, val photoThumbnailUri: Uri? = null, var absolutePath : String? = null)


class PhoneNumber(val phone: String, val type: Int, val label: String, var normalizedNumber: String? = null, var isPrimary: Boolean = false) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun toString(): String {
        return "Phone number: $phone   Type: $type  Label: $label  Normalized number: $normalizedNumber Is primary: $isPrimary"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(phone)
        parcel.writeInt(type)
        parcel.writeString(label)
        parcel.writeString(normalizedNumber)
        parcel.writeByte(if (isPrimary) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<PhoneNumber> {
        override fun createFromParcel(parcel: Parcel): PhoneNumber {
            return PhoneNumber(parcel)
        }

        override fun newArray(size: Int): Array<PhoneNumber?> {
            return arrayOfNulls(size)
        }
    }


}


fun Context.queryCursor(
    uri: Uri,
    projection: Array<String>?,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    callback: (cursor: Cursor) -> Unit
) {
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    callback(cursor)
                } while (cursor.moveToNext())
            }
        }
    } catch (e: Exception) {
        Log.e("Context", e.toString())
    }
}

object ContactApi {

    fun getContacts(context: Context, callback: (contact: Contacts) -> Unit = {}): List<Contacts> {

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
        )
        val _contacts = arrayListOf<Contacts>()
        context.queryCursor(
            ContactsContract.Contacts.CONTENT_URI, projection,
            sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC") { cursor ->
            val id: String = cursor.getString(0)
            val name: String? = cursor.getString(1)
            val hasNumber: Int = cursor.getInt(2)
            val starStatus: Int = cursor.getInt(3)
            val uriString: String? = cursor.getString(4)
            val photoThumbnailUri: Uri? = if (uriString == null) null else Uri.parse(uriString)
            Log.i("ContactApi", "getContacts: photo uri = $photoThumbnailUri")
            if (hasNumber > 0) {
                val phones = arrayListOf<PhoneNumber>()
                context.queryCursor(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                    ),
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf<String>(id),

                    ) { phoneCursor ->
                    val phoneNumber: String = phoneCursor.getString(0)
                    val type: Int = phoneCursor.getInt(1)
//                    val typeString = context.getPhoneTypeLabel(type)

//                    Log.d(TAG, "Name: $name, Phone Number: $phoneNumber")

                    phones.add(PhoneNumber(phoneNumber, type, "Mobile"))
                }
//                Log.d(TAG, "Name: $name")
                name?.let {
                    callback(Contacts(id, name, phones, starStatus == 1, photoThumbnailUri))

                    _contacts.add(Contacts(id, name, phones, starStatus == 1, photoThumbnailUri))

//                    CallLogApi.getCallLogByContact(context, id.toInt())

                }
            } else {
//                Log.d(TAG, "Name: $name, Phone Number: N/A")
                name?.let {

                    _contacts.add(Contacts(id, name, isStar = starStatus == 1, photoThumbnailUri = photoThumbnailUri))
                    callback(_contacts.last())
                }

            }
        }

//        contacts.clear()
//        contacts.addAll(_contacts)

        return _contacts
    }


    @SuppressLint("Range")
    private fun getRawContactId(context: Context, contactId: String): String? {
        var res = ""
        val uri = ContactsContract.RawContacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.RawContacts._ID)
        val selection = ContactsContract.RawContacts.CONTACT_ID + " = ?"
        val selectionArgs = arrayOf(contactId)
        val c: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (c != null && c.moveToFirst()) {
            res = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID))
            c.close()
        }
        return res
    }

    fun writeDisplayPhoto(context: Context, contactId: String, photo: ByteArray?) {

        val rawContactId = getRawContactId(context, contactId)?.toLong()

        val rawContactPhotoUri = Uri.withAppendedPath(
            rawContactId?.let { ContentUris.withAppendedId(RawContacts.CONTENT_URI, it) },
            RawContacts.DisplayPhoto.CONTENT_DIRECTORY
        )
        val fd: AssetFileDescriptor? =
            context.contentResolver.openAssetFileDescriptor(rawContactPhotoUri, "rw")

        fd?.use {
            fd.createOutputStream().use { os ->
                os.write(photo)
            }
        }
    }

    fun updatePhoto(context: Context, rawId: String) {
        var rawContactUri: Uri? = null
        context.contentResolver.query(
            RawContacts.CONTENT_URI, arrayOf<String>(RawContacts._ID),
            RawContacts.CONTACT_ID + " = " + rawId,
            null,
            null
        )?.use { rawContactCursor ->
            if (!rawContactCursor.isAfterLast) {
                rawContactCursor.moveToFirst()
                rawContactUri =
                    RawContacts.CONTENT_URI.buildUpon().appendPath("" + rawContactCursor.getLong(0))
                        .build()
            }
        }
    }
}
