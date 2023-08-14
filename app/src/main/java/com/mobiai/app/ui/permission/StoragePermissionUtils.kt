package com.mobiai.app.ui.permission
import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import com.mobiai.app.ui.permission.CurrentAPI
import com.mobiai.app.ui.permission.PermissionUtils

/**
  *  this class is used for check/ request device shared-storage permission on all android version
 **/
object StoragePermissionUtils {

    /**
     *  check read_permission to access media files that other apps create
     */
     fun isAPI33OrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun requestNotifyPermission(resultLauncher: ActivityResultLauncher<Array<String>>){
        PermissionUtils.requestMultiplePermission(
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            resultLauncher
        )
    }

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