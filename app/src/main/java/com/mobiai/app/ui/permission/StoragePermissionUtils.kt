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
    fun requestPhoneCallLogPermission(resultLauncher: ActivityResultLauncher<Array<String>>){
        PermissionUtils.requestMultiplePermission(
            arrayOf(Manifest.permission.READ_CALL_LOG),
            resultLauncher
        )
    }
    fun requestPhonePermission(resultLauncher: ActivityResultLauncher<Array<String>>){
        PermissionUtils.requestMultiplePermission(
            arrayOf(Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,Manifest.permission.ANSWER_PHONE_CALLS),
            resultLauncher
        )
    }
    fun requestCameraPermission(resultLauncher: ActivityResultLauncher<Array<String>>){
        PermissionUtils.requestMultiplePermission(
            arrayOf(Manifest.permission.CAMERA),
            resultLauncher
        )
    }
    fun requestSmsPermission(resultLauncher: ActivityResultLauncher<Array<String>>){
        PermissionUtils.requestMultiplePermission(
            arrayOf(Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS),
            resultLauncher
        )
    }

    fun requestAudioPermission(resultLauncher: ActivityResultLauncher<Array<String>>){
        PermissionUtils.requestMultiplePermission(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            resultLauncher
        )
    }

    fun requestContactPermission(resultLauncher: ActivityResultLauncher<Array<String>>){
        PermissionUtils.requestMultiplePermission(
            arrayOf(Manifest.permission.READ_CONTACTS),
            resultLauncher
        )
    }
}