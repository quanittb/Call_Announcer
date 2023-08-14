package com.mobiai.app.ultils

import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.mobiai.app.services.MyNotificationListenerService


object NotificationUtils {
    fun areNotificationsEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            Log.d("TAG", "areNotificationsEnabled: ")
            notificationManager != null && notificationManager.areNotificationsEnabled()
        } else {
            Log.d("TAG", "areNotificationsEnabled 111: ")
            areNotificationsEnabledPreOreo(context)
        }
    }
    fun isNotificationListenerPermissionGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val packageName = context.packageName
            val uid = context.applicationInfo.uid

            // Mã số 43 tương ứng với OP_BIND_NOTIFICATION_LISTENER_SERVICE trong AppOpsManager
            val mode = appOps.checkOpNoThrow(43.toString(), uid, packageName)

            return mode == AppOpsManager.MODE_ALLOWED
        }

        return false
    }
     fun isNotificationListenerEnabled(context: Context): Boolean {
        val componentName = ComponentName(context, MyNotificationListenerService::class.java)
        val flat: String =
            Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return flat.contains(componentName.flattenToString())
    }


    private fun areNotificationsEnabledPreOreo(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid

        // Ký hiệu số 11 tương ứng với OP_POST_NOTIFICATION trong AppOpsManager
        val appOpsMode = appOps.checkOpNoThrow(11.toString(), uid, pkg)
        return appOpsMode == AppOpsManager.MODE_ALLOWED
    }
}
