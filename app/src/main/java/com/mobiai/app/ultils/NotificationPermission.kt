package com.mobiai.app.ultils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class NotificationPermission(val context: Context) {
    private fun isAPI33OrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun hasPostNotificationGranted(): Boolean {
        if (isAPI33OrHigher() && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    fun requestRuntimeNotificationPermission(permissionLauncher: ActivityResultLauncher<String>) {
        permissionLauncher.launch( Manifest.permission.POST_NOTIFICATIONS)
    }

}
