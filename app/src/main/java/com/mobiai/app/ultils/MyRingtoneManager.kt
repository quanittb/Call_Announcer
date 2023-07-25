package com.mobiai.app.ultils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.loader.content.CursorLoader
import com.mobiai.R
import com.mobiai.app.ui.model.ItemDeviceRingtone
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ultility.UriUtils


object MyRingtoneManager {

    private val TAG: String = MyRingtoneManager::javaClass.name

    fun getDeviceRingtone(context: Context): MutableList<ItemDeviceRingtone> {
        val ringtoneManager = RingtoneManager(context)
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE)
        val cursor = ringtoneManager.cursor
        val defaultRingtones = mutableListOf<ItemDeviceRingtone>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(RingtoneManager.ID_COLUMN_INDEX)
                val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                val uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
                val ringtoneUri = ContentUris.withAppendedId(Uri.parse(uri), id.toLong())
                Log.i(TAG, "getDeviceRingtone: $ringtoneUri")

                val itemDeviceRingtone = ItemDeviceRingtone(
                    id,
                    title,
                    context.resources.getString(R.string.device_ringtone),
                    uri = ringtoneUri,
                    getDurationFromUri(context, ringtoneUri),
                    type = Constant.DEVICE_RINGTONE
                )
                itemDeviceRingtone.isChoose = id == SharedPreferenceUtils.saved_ringtone_id && getPathFromUri(
                    context,
                    ringtoneUri
                ) == SharedPreferenceUtils.saved_ringtone_path
                defaultRingtones.add(itemDeviceRingtone)
            } while (cursor.moveToNext())

            cursor.close()
        }

        return defaultRingtones
    }

    fun setDefaultRingtone(context: Context, ringtoneItem: ItemDeviceRingtone) {
        val ringtoneManager = RingtoneManager(context)
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE)
        val cursor = ringtoneManager.cursor
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val uriString =
                    cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(
                        RingtoneManager.ID_COLUMN_INDEX
                    )

                val uri = Uri.parse(uriString)
                if (uri == (ringtoneItem as ItemDeviceRingtone).uri) {
                    val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                    val ringtone = ringtoneManager.getRingtone(cursor.position)
                    RingtoneManager.setActualDefaultRingtoneUri(
                        context, RingtoneManager.TYPE_RINGTONE, uri
                    )
                }
            } while (cursor.moveToNext())

            cursor.close()
        }
    }

    fun setRingtone(context: Context, pickedItemRingtone: ItemDeviceRingtone): Uri? {
//        val cursor =
//            context.contentResolver.query(pickedItemRingtone.uri, null, null, null, null)
//        if (cursor != null && cursor.moveToFirst()) {
//            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
//            val id = cursor.getLong(idColumn)
//            val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
//
//            RingtoneManager.setActualDefaultRingtoneUri(
//                context,
//                RingtoneManager.TYPE_RINGTONE,
//                uri
//            )
//
//            return uri
//            //  requireContext().contentResolver.update(uri, values, null, null)
//        }
//        return null

        RingtoneManager.setActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_RINGTONE,
            pickedItemRingtone.uri
        )

        if (RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE) == pickedItemRingtone.uri){
            return pickedItemRingtone.uri
        }
        return null

    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media._ID)
        val loader = CursorLoader(context, contentUri, proj, null, null, null)
        val cursor: Cursor = loader.loadInBackground()!!
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }


    fun getSongIdFromMediaStore(songPath: String, context: Context): Long {
        var id: Long = 0
        val cr = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.DATA
        val selectionArgs = arrayOf(songPath)
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = cr.query(
            uri, projection,
            "$selection=?", selectionArgs, sortOrder
        )
        Log.d(TAG, songPath)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                id = cursor.getString(idIndex).toLong()
            }
        }
        return id
    }

    fun getSongInfoFromUri(context: Context, uri: Uri): ItemDeviceRingtone? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val artistTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: ""
        val id = getSongIdFromMediaStore(UriUtils.getPathFromUriTryMyBest(context, uri), context)
        retriever.release()

        return ItemDeviceRingtone(
            id.toInt(),
            title,
            artistTitle,
            uri,
            duration,
            Constant.DEVICE_RINGTONE
        )
    }

    fun getDurationFromUri(context: Context, uri: Uri): String {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val duration =
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        val durationString = String.format("%02d:%02d", minutes, seconds)
        mmr.release()
        return durationString
    }


    fun getPathFromUri(context: Context, uri: Uri): String {
        try {
            var filePath: String = ""
            val projection = arrayOf(MediaStore.Audio.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                filePath = cursor.getString(columnIndex)

                cursor.close()
            }
            return filePath
        } catch (e : Exception) {
            return UriUtils.getPathFromUriTryMyBest(context, uri)
        }

    }


    fun getSongFromId(context: Context, songId: Int): ItemDeviceRingtone? {

        val contentResolver = context.contentResolver
        val songUri =
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId.toLong())
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )
        val cursor = contentResolver.query(songUri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))

                return ItemDeviceRingtone(songId, title, artist, songUri, getDurationFromUri(context,songUri), Constant.DEVICE_RINGTONE)
            }
        }
        return null
    }


}
