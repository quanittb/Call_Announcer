package com.mobiai.base.basecode.ultility.getfilefromuri;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static final String FILE_AUTHORITY = "com.trustedapp.pdfreaderpdfviewer.fileprovider";
    public static String getPathFromUri(final Context context, final Uri uri) {
        Log.e("getPathFromUri",uri.getPath());
        final boolean isKitKat = VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            //Documents/DocumentsScanner/anhanh.pdf
            //content://com.vsmart.android.externalstorage.documents/document/home%3ADocumentsScanner%2Fanhanh.pdf
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }else if (isGoogleGmailUri(uri)){
            return getPathUriGmail(context,uri);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getPathFromOutlook(Context context, Uri uri){
        // uri2 = Uri.parse("content://com.microsoft.office.outlook.fileprovider/outlookfile/data/user/0/com.microsoft.office.outlook/cache/file-download/file--1723028522/Sachvui.Com-Phi-ly-tri-Dan-Ariely-scan.pdf");
     ///outlookfile/data/data/com.microsoft.office.outlook/cache/file-download/file-1754115030/Doc 19-02-2021 15_00 CH.pdf
        String path = uri.getPath();
        path = path.replace("/outlookfile/data","storage/emulated/0");


        return path;
    }

   public static String getPathUriGmail(Context context, Uri uri){

        InputStream is = null;
        FileOutputStream os = null;
        String fullPath = null;

        try {

            String scheme = uri.getScheme();
            String name = null;

         if (scheme.equals("content")) {
                Cursor cursor = context.getContentResolver().query(uri, new String[] {
                        MediaStore.MediaColumns.DISPLAY_NAME
                }, null, null, null);
                cursor.moveToFirst();
                int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex);
                }
            } else {
                return null;
            }

            if (name == null) {
                return null;
            }

            int n = name.lastIndexOf(".");
            String fileName, fileExt;

            if (n == -1) {
                return null;
            }

            fullPath = context.getCacheDir()+ "/" + name;

            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(fullPath);

            byte[] buffer = new byte[4096];
            int count;
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            os.close();
            is.close();
        } catch (Exception e) {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e1) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e1) {
                }
            }
            if (fullPath != null) {
                File f = new File(fullPath);
                f.delete();
            }
            e.printStackTrace();
        }





        return fullPath;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Gmail.
     */
    public static boolean isGoogleGmailUri(Uri uri) {
        return "com.google.android.gm.sapi".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Outlook email.
     */
    public static boolean isOutlookUri(Uri uri) {
        return "com.microsoft.office.outlook.fileprovider".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is WhatApp.
     */
    public static boolean isWhatAppUri(Uri uri) {
        return "com.whatsapp.provider.media".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Telegram.
     */
    public static boolean isTelegramUri(Uri uri) {
        return "org.telegram.messenger.provider".equals(uri.getAuthority());
    }



    public static String formatDateToHumanReadable(Long l) {
        return new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(new Date(l.longValue()));
    }

    public static String removePdfExtension(String str) {
        int lastIndexOf = str.lastIndexOf(System.getProperty("file.separator"));
        if (lastIndexOf != -1) {
            str = str.substring(lastIndexOf + 1);
        }
        int lastIndexOf2 = str.lastIndexOf(".");
        if (lastIndexOf2 == -1) {
            return str;
        }
        return str.substring(0, lastIndexOf2);
    }

    public static void shareFileImage(Context context, ArrayList<Uri> listFile) {

        if (listFile.size() > 0) {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intentShareFile.setType("image/*") ;
            intentShareFile.putExtra(Intent.EXTRA_STREAM, listFile);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
//            intentShareFile.putExtra(Intent.EXTRA_TEXT, ArrayList{"Sharing File..."});
            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }
    public static void saveBitMap(Bitmap bitmap, String imagePath) {
        Log.e("saveBitMap",imagePath);
        OutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(imagePath));
            bitmap.compress(CompressFormat.JPEG, 85, bufferedOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static boolean isFileNameValid(String str) {
        String trim = str.trim();
        return !TextUtils.isEmpty(trim) && trim.matches("[a-zA-Z0-9-_ ]*");
    }

    public static void deletePdfFiles(String str) {
        File file = new File(str);
        if (file.exists() && file.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            sb.append("find ");
            sb.append(str);
            sb.append(" -xdev -mindepth 1 -delete");
            try {
                Runtime.getRuntime().exec(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Uri getImageUriFromPath(String str) {
        return Uri.fromFile(new File(str.replace(".pdf", ".jpg")));
    }

    public static boolean isThumbnailPresent(Context context, String str) {
        String name = new File(str).getName();
        StringBuilder sb = new StringBuilder();
        sb.append(context.getCacheDir());
        sb.append("/Thumbnails/");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append(removePdfExtension(name));
        sb3.append(".jpg");
        return new File(sb3.toString()).exists();
    }

    public static MemoryInfo getAvailableMemory(Context context) {
        @SuppressLint("WrongConstant") ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

}
