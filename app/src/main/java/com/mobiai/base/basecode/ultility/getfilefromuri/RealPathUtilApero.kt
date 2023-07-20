package com.mobiai.base.basecode.ultility.getfilefromuri

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


object RealPathUtilApero {

    private val listAppProvider = listOf("com.skype.raider.fileprovider")

    fun getRealPath(context: Context, fileUri: Uri): String? {
        return getRealPathFromURIAPI19New(context, fileUri)
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author Niks
     */
    @SuppressLint("NewApi")
    fun getRealPathFromURIAPI19(context: Context, uri: Uri): String? {
        var filePath: String? = ""
        try {
            if (DocumentsContract.isDocumentUri(context, uri)) { // DocumentProvider
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val splitDocId =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = splitDocId[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory()
                            .toString() + "/" + splitDocId[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    var cursor: Cursor? = null
                    try {
                        cursor = context.contentResolver.query(
                            uri,
                            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                            null,
                            null,
                            null
                        )
                        cursor!!.moveToNext()
                        val fileName = cursor.getString(0)
                        val path = Environment.getExternalStorageDirectory()
                            .toString() + "/Download/" + fileName
                        if (!TextUtils.isEmpty(path)) {
                            return path
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        cursor?.close()
                    }
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads"),
                        java.lang.Long.valueOf(id)
                    )

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if (!TextUtils.isEmpty(uri.scheme) && "content".equals(
                    uri.scheme!!,
                    ignoreCase = true
                )
            ) {
//            filePath = parseForSomeSpecialApps(uri)
                if (TextUtils.isEmpty(filePath)) {
                    filePath = if (isGooglePhotosUri(uri)) {
                        uri.lastPathSegment
                    } else { // cache file to this app cache dir
                        getCacheFilePath(uri, context)
                    }
                }
                // Return the remote address
//            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
//                context,
//                uri,
//                null,
//                null
//            )
            } else if (!TextUtils.isEmpty(uri.scheme) && "file".equals(
                    uri.scheme!!,
                    ignoreCase = true
                )
            ) {
                return uri.path
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            val id = DocumentsContract.getDocumentId(uri)
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
                && id.startsWith("msf:")
            ) {
                val splitDocId: List<String> = id.split(":")
                val selection = "_id=?"
                val selectionArgs = arrayOf(splitDocId[1])
                return getDataColumn(
                    context,
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    selection,
                    selectionArgs
                )
            }
        }
        return filePath
    }

    private fun getRealPathFromURIAPI19New(context: Context?, uri: Uri): String? {
        // DocumentProvider
        // ExternalStorageProvider
        try {
            when {
                isExternalStorageDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return context!!.getExternalFilesDir(split[1])!!.path
                    }
                }
                isGoogleGmailUri(uri) -> {
                    return getPathUriGmail(context!!, uri)
                }
                isWhatAppUri(uri) -> {
                    return getPathUriGmail(context!!, uri)
                }
                isTelegramUri(uri) -> {
                    val nameFile = getNameFile(context!!, uri, null, null)
                    val path = context.getExternalFilesDir("/Telegram/Telegram Documents")!!.path + File.separator + nameFile
                    val file = File(path)
                    return if (file.exists()) path else null
                }
                isDownloadsDocument(uri) -> {
                    val fileName = getNameFile(context!!, uri, null, null)
                    if (fileName !=null) {
                        return context.getExternalFilesDir("/Download")!!.path + File.separator + fileName
                    }
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                }
                isMediaDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return getDataColumn(context!!, contentUri, selection, selectionArgs)
                }
                isGoogleDriveUri(uri) -> {
                    return getDriveFilePath(uri, context!!)
                }
            }
            return null
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    private fun parseForSomeSpecialApps(fileUri: Uri): String {
        var filePath = ""
        if (!TextUtils.isEmpty(fileUri.toString())) {
            val fileUriString: String = fileUri.toString()
            if (fileUriString.contains("com.zing.zalo.provider/external_files/")) {
                filePath =
                    "/storage/emulated/0/" + fileUriString.substring(fileUriString.indexOf("/external_files/") + 16)
            } else if (fileUriString.contains("org.telegram.messenger.provider")) {
                filePath =
                    "/storage/emulated/0/" + fileUriString.substring(fileUriString.indexOf("/media/") + 7)
            }
            filePath = try {
                URLDecoder.decode(filePath, "UTF-8")
            } catch (unsupportedEncodingException: UnsupportedEncodingException) {
                unsupportedEncodingException.printStackTrace()
                ""
            }
            Log.e("isCanParseSpecialApp ", " filePath = $filePath")
        }
        return filePath
    }

    private fun isNeedToCache(uri: Uri): Boolean {
        if (isGoogleDrive(uri)
            || listAppProvider.contains(uri.authority)
        ) {
            return true
        }
        return false
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is GoogleDrive.
     */
    private fun isGoogleDrive(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    private fun getCacheFilePath(uri: Uri, context: Context?): String {
        if (context == null || context.cacheDir == null || context.contentResolver == null) return ""
        var file: File? = null
        var c: Cursor? = null
        try {
            val contentResolver = context.contentResolver
            val cacheDir = context.cacheDir
            c = contentResolver.query(uri, null, null, null, null) ?: return ""
            /*
             * Get the column indexes of the data in the Cursor,
             *     * move to the first row in the Cursor, get the data,
             *     * and display it.
             * */
            val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (c.count > 0 && c.moveToFirst()) {
//            val size = c.getLong(sizeIndex).toString()
                val name = c.getString(nameIndex)
                file = File(cacheDir, name)
                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable = inputStream!!.available()

                //int bufferSize = 1024;
                val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
//                ALog.e("File Size", "Size " + file.length())
//                ALog.e("File Path", "Path " + file.path)
//                ALog.e("File Size", "Size " + file.length())
            }
        } catch (e: Exception) {
            file = null
//            Firebase.analytics.logEvent(Constants.logEventName.GET_CACHE_FILE_PATH) {
//                param(
//                    "exception stackTrace",
//                    "" + if (TextUtils.isEmpty(e.stackTraceToString())) "exception stackTrace == null" else e.printStackTrace()
//                )
//            }
        } finally {
            if (c != null && !c.isClosed) {
                c.close()
            }
        }
        return if (file != null) file.path else ""
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author Niks
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun getPathFromData(context: Context?, data: Uri): String? {
        var pathFile: String?
        try {
            Log.e("DataPDF", "data:$data")
            pathFile = getRealPath(context!!, data)
            if (pathFile == null) pathFile = getPathFromUri(context, data)
            if (pathFile == null) {
                pathFile = data.toString()
                val index = pathFile.indexOf(":")
                if (index > 0) {
                    pathFile = pathFile.substring(index + 3)
                }
                pathFile = Uri.decode(pathFile)
            }
            if (!TextUtils.isEmpty(pathFile) && pathFile!!.contains("/raw:")) {
                pathFile = pathFile.substring(pathFile.indexOf("/raw:") + 5)
            }
            Log.e("DataPDF", "pathFile:$pathFile")
        } catch (e: java.lang.Exception) {
            Log.e("DataPDF", e.message!!)
            pathFile = Uri.decode(data.path).toString().replace("/root", "")
                .replace("external/", "storage/emulated/0/")
                .replace("external_files/", "storage/emulated/0/")
            if (pathFile.contains("SkypeDownload")) {
                val lastIndex = pathFile.lastIndexOf("_")
                pathFile = pathFile.substring(0, lastIndex) + ".pdf"
                pathFile = pathFile.replace("SkypeDownload/", "storage/emulated/0/Download/")
            } else if (pathFile.contains("Telegram")) {
                pathFile = pathFile.replace("device_storage", "storage/emulated/0")
            } else if (pathFile.contains("org.telegram.messenger") || pathFile.contains("media")) {
                pathFile = pathFile.replace("media", "storage/emulated/0")
            }

            ///document/downloads:tessss.pdf
            Log.e("DataPDF", "Exception:$pathFile")
        }
        return pathFile
    }

    private fun getPathFromUri(context: Context?, uri: Uri): String? {
        Log.e("getPathFromUri", uri.path!!)
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return context?.let {
                    getDataColumn(
                        it,
                        contentUri,
                        null,
                        null
                    )
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return context?.let {
                    getDataColumn(
                        it,
                        contentUri,
                        selection,
                        selectionArgs
                    )
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else context?.let {
                getDataColumn(
                    it,
                    uri,
                    null,
                    null
                )
            }
        } else if (isGoogleGmailUri(uri)) {
            return context?.let { getPathUriGmail(it, uri) }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun isGoogleGmailUri(uri: Uri): Boolean {
        return "com.google.android.gm.sapi" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is WhatApp.
     */
    private fun isWhatAppUri(uri: Uri): Boolean {
        return "com.whatsapp.provider.media" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Telegram.
     */
    private fun isTelegramUri(uri: Uri): Boolean {
        return "org.telegram.messenger.provider" == uri.authority
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority
                || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    private fun getNameFile(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DISPLAY_NAME
        try {
            cursor = context.contentResolver.query(uri!!, null, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } catch (e: java.lang.Exception) {
            Log.e("getNameFile", "error: " + e.message)
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun getDriveFilePath(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        val file = File(context.cacheDir, name)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read: Int
            val maxBufferSize = 1024 * 1024
            val bytesAvailable = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)
        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }

    private fun getPathUriGmail(context: Context, uri: Uri): String? {
        var `is`: InputStream? = null
        var os: FileOutputStream? = null
        var fullPath: String? = null
        try {
            val scheme = uri.scheme
            var name: String? = null
            if (scheme == "content") {
                val cursor = context.contentResolver.query(
                    uri, arrayOf(
                        MediaStore.MediaColumns.DISPLAY_NAME
                    ), null, null, null
                )
                cursor!!.moveToFirst()
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex)
                }
                cursor.close()
            } else {
                return null
            }
            if (name == null) {
                return null
            }
            val n = name.lastIndexOf(".")
            if (n == -1) {
                return null
            }
            fullPath = context.cacheDir.toString() + "/" + name
            `is` = context.contentResolver.openInputStream(uri)
            os = FileOutputStream(fullPath)
            val buffer = ByteArray(4096)
            var count: Int
            while (`is`!!.read(buffer).also { count = it } > 0) {
                os.write(buffer, 0, count)
            }
            os.close()
            `is`.close()
        } catch (e: java.lang.Exception) {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e1: java.lang.Exception) {
                }
            }
            if (os != null) {
                try {
                    os.close()
                } catch (e1: java.lang.Exception) {
                }
            }
            if (fullPath != null) {
                val f = File(fullPath)
                f.delete()
            }
            e.printStackTrace()
        }
        return fullPath
    }
}
