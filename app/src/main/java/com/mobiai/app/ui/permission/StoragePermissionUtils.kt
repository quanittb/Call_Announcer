package com.mobiai.base_storage.permission
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import com.mobiai.app.ui.permission.CurrentAPI
import com.mobiai.app.ui.permission.PermissionUtils

/**
  *  this class is used for check/ request device shared-storage permission on all android version
 **/
object StoragePermissionUtils {

    /**
     *  check read_permission to access media files that other apps create
     */
    fun isReadStorageGranted(context: Context) : Boolean{
        // android 13 or higher
        return if (CurrentAPI.isApi33orHigher()){
            PermissionUtils.isPermissionGrandted(
                context,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            )

        }else {
            //below android 13
            PermissionUtils.isPermissionGrandted(
                context,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }

    /**
     * declare read_permission to access media files that other apps create
     */
    fun requestReadStoragePermission( resultLauncher: ActivityResultLauncher<String>){
        if (CurrentAPI.isApi33orHigher()) {
            //  android 13 or higher
            PermissionUtils.requestPermission(
                (Manifest.permission.READ_MEDIA_AUDIO),
                resultLauncher
            )
        }else{
            //  android 12 or lower
            PermissionUtils.requestPermission(
                (Manifest.permission.READ_EXTERNAL_STORAGE),
                resultLauncher
            )
        }

    }


    /**
     * in android 9 or lower, app must have write_permission to modify media files
     */
    fun isWriteStorageGranted(context: Any?): Boolean{

        return if (CurrentAPI.isApi33orHigher()){
            (PermissionUtils.isPermissionGrandted(
                context,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
            ))

        }else {
            (PermissionUtils.isPermissionGrandted(
                context,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ))
        }

    }

    /**
     * request write_permission in android 9 or lower to modify media file
     */
    fun requestWriteStoragePermission(resultLauncher: ActivityResultLauncher<String>){

        if (CurrentAPI.isApi33orHigher()) {
            //  android 13 or higher
            PermissionUtils.requestPermission(
                (Manifest.permission.READ_MEDIA_AUDIO),
                resultLauncher
            )
        }else{
            //  android 12 or lower
            PermissionUtils.requestPermission(
                (Manifest.permission.WRITE_EXTERNAL_STORAGE),
                resultLauncher
            )
        }

    }

    fun isHasStoragePermission(context: Context) : Boolean{
        // android 13 or higher
        return if (CurrentAPI.isApi33orHigher()){
            PermissionUtils.isPermissionGrandted(
                context,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
            )

        }else {
            //below android 13
            PermissionUtils.isPermissionGrandted(
                context,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }
    fun requestStoragePermission(resultLauncher: ActivityResultLauncher<Array<String>>){
        if (CurrentAPI.isApi33orHigher()) {
            //  android 13 or higher
            PermissionUtils.requestMultiplePermission(
                (arrayOf(Manifest.permission.READ_MEDIA_AUDIO)),
                resultLauncher
            )
        }else{
            //  android 12 or lower
            PermissionUtils.requestMultiplePermission(
                (arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)),
                resultLauncher
            )
        }

    }

    fun goToSetting(context: Context){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val  uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
}