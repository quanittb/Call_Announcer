package com.mobiai.app.ultils

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.CountDownTimer
import android.util.Log


class FlashlightHelper(context: Context) {
    private val TAG: String = FlashlightHelper::javaClass.name
    private val cameraManager: CameraManager? =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
    private var cameraId: String? = null
    private var isFlashOn: Boolean = false
    private var blinkTask: BlinkTask? = null

    init {
        try {
            cameraId = cameraManager?.cameraIdList
                ?.find {
                    cameraManager.getCameraCharacteristics(it)
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "cameramanager is null: ", e)
            e.printStackTrace()
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: FlashlightHelper? = null

        fun getInstance(context: Context): FlashlightHelper =
            instance ?: synchronized(this) {
                instance ?: FlashlightHelper(context).also { instance = it }
            }
    }

    // hàm này sử dụng khi là click button
    fun toggleFlash(): Boolean {
        if (cameraId == null) {
            return false
        }
        isFlashOn = !isFlashOn
        try {
            cameraManager?.setTorchMode(cameraId!!, isFlashOn)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return isFlashOn
    }
    //end hàm

    fun blinkFlash(period: Long) {
        blinkTask?.cancel()
        blinkTask = cameraManager?.let { cameraId?.let { it1 -> BlinkTask(it, it1, period) } }
        blinkTask?.start()
    }

    fun stopBlink() {
        Log.e("CheckBlinkTask", "$blinkTask")
        blinkTask?.cancel()
        stopFlash()
    }

    fun stopFlash() {
        Log.e("CheckcameraId", "$cameraId")
        isFlashOn = false
        try {
            cameraId?.let { cameraManager?.setTorchMode(it, false) }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "cameraId =  $cameraId ", e)
            e.printStackTrace()
        }
    }

    private inner class BlinkTask(
        val cameraManager: CameraManager,
        val cameraId: String,
        val period: Long
    ) : CountDownTimer(Long.MAX_VALUE, period) {
        private var isOn: Boolean = false

        override fun onFinish() {}

        override fun onTick(millisUntilFinished: Long) {
            isOn = !isOn
            try {
                cameraManager.setTorchMode(cameraId, isOn)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }
}
