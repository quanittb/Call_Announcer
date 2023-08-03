package com.mobiai.app.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
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
    private var goToSettingDialogSms: GotosettingDialog? = null
    private var goToSettingDialogContact: GotosettingDialog? = null
    private var goToSettingDialogAudio: GotosettingDialog? = null

    private var isGotoSettingSms = false
    private var isGotoSettingContact = false
    private var isGotoSettingAudio = false
    override fun initView() {
        checkPermission()
        binding.icClose.setOnSafeClickListener(500){
            handlerBackPressed()
        }
        binding.cvAllow.setOnSafeClickListener(1000){
            StoragePermissionUtils.requestSmsPermission(requestMultipleSmsPermissionsLauncher)
        }
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_SMS,
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
                if (permission == Manifest.permission.READ_SMS  || permission == Manifest.permission.RECEIVE_SMS){
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
    private fun checkPermissionOnResume(permission:String) {
        if (permission == Manifest.permission.READ_SMS || permission == Manifest.permission.RECEIVE_SMS){
            isGotoSettingSms = false
            if (ActivityCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED){
                binding.icSelectSms.visible()
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    RxBus.publish(IsTurnOnSms())
                    Handler().postDelayed({
                        handlerBackPressed()
                    },100)
                }
                else{
                    StoragePermissionUtils.requestContactPermission(requestMultipleContactPermissionsLauncher)
                }
            }
            else{
                checkPermission()
                showGotoSettingSmsDialog()
            }
        }

        else if (permission == Manifest.permission.READ_CONTACTS){
            isGotoSettingContact = false
            if (ActivityCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED) {
                binding.icSelectContact.visible()
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    RxBus.publish(IsTurnOnSms())
                    Handler().postDelayed({
                        handlerBackPressed()
                    },100)
                }
                else{
                    StoragePermissionUtils.requestAudioPermission(requestMultipleAudioPermissionsLauncher)
                }
            }
            else{
                checkPermission()
                showGotoSettingContactDialog()
            }
        }


        else if (permission == Manifest.permission.RECORD_AUDIO){
            isGotoSettingAudio = false
            if (ActivityCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED){
                binding.icSelectAudio.visible()
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
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
    private fun showGotoSettingSmsDialog() {
        if (goToSettingDialogSms == null) {
            goToSettingDialogSms = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                isGotoSettingSms = true
                gotoSetting()
            }
        }
        if (!goToSettingDialogSms!!.isShowing) {
            goToSettingDialogSms!!.show()
        }
    }
    private fun showGotoSettingContactDialog() {
        if (goToSettingDialogContact==null){
            goToSettingDialogContact = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                isGotoSettingContact = true
                gotoSetting()
            }
        }

        if (!goToSettingDialogContact!!.isShowing) {
            goToSettingDialogContact!!.show()
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
        if (!goToSettingDialogAudio!!.isShowing) {
            goToSettingDialogAudio!!.show()
        }
    }

    private val requestMultipleSmsPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo sms
            isGotoSettingSms = false
            binding.icSelectSms.visible()
            StoragePermissionUtils.requestContactPermission(requestMultipleContactPermissionsLauncher)
        } else {
            showGotoSettingSmsDialog()
        }
    }

    private val requestMultipleContactPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo contact
            isGotoSettingContact = false
            binding.icSelectContact.visible()
            SharedPreferenceUtils.isUnknownNumberSms = true
            SharedPreferenceUtils.isReadNameSms = true
            StoragePermissionUtils.requestAudioPermission(requestMultipleAudioPermissionsLauncher)
        } else {
            showGotoSettingContactDialog()
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
            RxBus.publish(IsTurnOnSms())
            handlerBackPressed()
        } else {
            showGotoSettingAudioDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isGotoSettingSms){
            checkPermissionOnResume(Manifest.permission.READ_SMS)
        }
        else if (isGotoSettingContact){
            checkPermissionOnResume(Manifest.permission.READ_CONTACTS)
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