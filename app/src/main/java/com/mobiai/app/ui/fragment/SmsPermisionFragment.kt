package com.mobiai.app.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
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
import com.mobiai.base_storage.permission.StoragePermissionUtils
import com.mobiai.databinding.FragmentPermissionSmsBinding

class SmsPermisionFragment :BaseFragment<FragmentPermissionSmsBinding>()
{
    companion object{
        fun instance() : SmsPermisionFragment{
            return newInstance(SmsPermisionFragment::class.java)
        }
    }
    private var goToSettingDialog: GotosettingDialog? = null
    override fun initView() {
        checkPermission()
        binding.icClose.setOnSafeClickListener(500){
            handlerBackPressed()
        }
        binding.cvAllow.setOnSafeClickListener(300){
            StoragePermissionUtils.requestSmsPermission(requestMultipleSmsPermissionsLauncher)
        }
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO
        )

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permission == Manifest.permission.READ_SMS || permission == Manifest.permission.SEND_SMS || permission == Manifest.permission.RECEIVE_SMS){
                    binding.icSelectSms.visible()
                }
                if (permission == Manifest.permission.READ_CONTACTS){
                    binding.icSelectContact.visible()
                }
                if (permission == Manifest.permission.RECORD_AUDIO){
                    binding.icSelectAudio.visible()
                }
            }
        }
    }
    private fun checkPermissionOnResume() {
        val permissions = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO
        )

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permission == Manifest.permission.READ_SMS || permission == Manifest.permission.SEND_SMS || permission == Manifest.permission.RECEIVE_SMS){
                    binding.icSelectSms.visible()
                }
                if (permission == Manifest.permission.READ_CONTACTS){
                    binding.icSelectContact.visible()
                }
                if (permission == Manifest.permission.RECORD_AUDIO){
                    binding.icSelectAudio.visible()
                }
            }
            else{
                showGotoSettingDialog()
                return
            }
        }
    }
    private fun showGotoSettingDialog() {
        if (goToSettingDialog == null) {
            goToSettingDialog = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                gotoSetting()
            }
        }
        if (!goToSettingDialog!!.isShowing) {
            goToSettingDialog!!.show()
        }
    }

    private val requestMultipleSmsPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo sms
            binding.icSelectSms.visible()
            StoragePermissionUtils.requestContactPermission(requestMultipleContactPermissionsLauncher)
        } else {
            showGotoSettingDialog()
        }
    }

    private val requestMultipleContactPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo contact
            binding.icSelectContact.visible()
            SharedPreferenceUtils.isUnknownNumberSms = true
            SharedPreferenceUtils.isReadNameSms = true
            StoragePermissionUtils.requestAudioPermission(requestMultipleAudioPermissionsLauncher)
        } else {
            showGotoSettingDialog()
        }
    }

    private val requestMultipleAudioPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo audio
            binding.icSelectAudio.visible()
            SharedPreferenceUtils.isTurnOnSmsNormal = true
            RxBus.publish(IsTurnOnSms())
            handlerBackPressed()
        } else {
            showGotoSettingDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isGoToSetting){
            isGoToSetting = false
            checkPermissionOnResume()
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