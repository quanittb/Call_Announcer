package com.mobiai.app.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.ads.control.admob.AppOpenManager
import com.mobiai.app.ui.dialog.GotosettingDialog
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.app.ultils.IsTurnOnSms
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.base.basecode.ultility.RxBus
import com.mobiai.app.ui.permission.StoragePermissionUtils
import com.mobiai.app.ultils.NotificationUtils
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.databinding.FragmentPermissionSmsBinding

class SmsPermisionFragment :BaseFragment<FragmentPermissionSmsBinding>()
{
    companion object{
        fun instance() : SmsPermisionFragment{
            return newInstance(SmsPermisionFragment::class.java)
        }
    }
    private var goToSettingDialogReadNotify: GotosettingDialog? = null
    private var goToSettingDialogContact: GotosettingDialog? = null
    private var goToSettingDialogAudio: GotosettingDialog? = null

    private var isGotoSettingReadNotify = false
    private var isGotoSettingContact = false
    private var isGotoSettingAudio = false
    override fun initView() {
        checkPermission()
        binding.icClose.setOnSafeClickListener(500){
            handlerBackPressed()
        }
        binding.cvAllow.setOnSafeClickListener(1000){
           // StoragePermissionUtils.requestSmsPermission(requestMultipleSmsPermissionsLauncher)
            requestPermissionReadNotify()
        }
    }

    private fun requestPermissionReadNotify(){
        if (!NotificationUtils.isNotificationListenerEnabled(requireContext())) {
            // Quyền truy cập thông báo chưa được cấp
            showGotoSettingReadNotifyDialog()
        } else {
            // Quyền truy cập thông báo đã được cấp
            //todo notify
            isGotoSettingReadNotify = false
            binding.icSelectNotify.visible()
            StoragePermissionUtils.requestAudioPermission(requestMultipleAudioPermissionsLauncher)
        }
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO
        )

        if (NotificationUtils.isNotificationListenerEnabled(requireContext())) {
            binding.icSelectNotify.visible()
        }
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permission == Manifest.permission.RECORD_AUDIO){
                    binding.icSelectAudio.visible()
                }
            }
        }
    }
    private fun checkPermissionOnResume(permission:String) {
        if (permission == Manifest.permission.RECORD_AUDIO){
            isGotoSettingAudio = false
            if (ActivityCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED){
                binding.icSelectAudio.visible()
                if (NotificationUtils.isNotificationListenerEnabled(requireContext())){
                    SharedPreferenceUtils.isTurnOnSmsNormal = true
                    RxBus.publish(IsTurnOnSms())
                    Handler().postDelayed({
                        handlerBackPressed()
                    },100)
                }
            }
            else{
                checkPermission()
                showGotoSettingAudioDialog()
            }
        }
    }
    private fun showGotoSettingReadNotifyDialog() {
        if (goToSettingDialogReadNotify == null) {
            goToSettingDialogReadNotify = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                isGotoSettingReadNotify = true
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                requireContext().startActivity(intent)
            }
        }
        goToSettingDialogReadNotify!!.gotoSetingNotify()

        if (!goToSettingDialogReadNotify!!.isShowing) {
            goToSettingDialogReadNotify!!.show()
        }
    }
    private fun showGotoSettingAudioDialog() {
        if (goToSettingDialogAudio==null){
            goToSettingDialogAudio = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                isGotoSettingAudio = true
                gotoSetting()
            }
        }
        goToSettingDialogReadNotify!!.gotoSetingAudio()
        if (!goToSettingDialogAudio!!.isShowing) {
            goToSettingDialogAudio!!.show()
        }
    }

    private val requestMultipleAudioPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo audio
            isGotoSettingAudio = false
            binding.icSelectAudio.visible()
            SharedPreferenceUtils.isTurnOnSmsNormal = true
            SharedPreferenceUtils.isUnknownNumberSms = true
            SharedPreferenceUtils.isReadNameSms = true
            RxBus.publish(IsTurnOnSms())
            handlerBackPressed()
        } else {
            showGotoSettingAudioDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        SharedPreferenceUtils.languageCode?.let { LanguageUtil.changeLang(it, requireContext()) }
        if (isGotoSettingReadNotify){
            isGotoSettingReadNotify = false
            if (NotificationUtils.isNotificationListenerEnabled(requireContext())) {
                binding.icSelectNotify.visible()
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    RxBus.publish(IsTurnOnSms())
                    Handler().postDelayed({
                        handlerBackPressed()
                    },100)
                }
                else{
                    StoragePermissionUtils.requestAudioPermission(requestMultipleAudioPermissionsLauncher)
                }
            } else {
                checkPermission()
                showGotoSettingReadNotifyDialog()
            }
        }
        else if (isGotoSettingAudio){
            checkPermissionOnResume(Manifest.permission.RECORD_AUDIO)
        }
        else{
            checkPermission()
        }
    }

    override fun handlerBackPressed() {
        super.handlerBackPressed()
        closeFragment(this)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPermissionSmsBinding = FragmentPermissionSmsBinding.inflate(inflater,container,false)
}