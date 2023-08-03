package com.mobiai.app.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.ads.control.admob.AppOpenManager
import com.mobiai.app.ui.dialog.GotosettingDialog
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.app.ultils.IsTurnOnCall
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.base.basecode.ultility.RxBus
import com.mobiai.base_storage.permission.StoragePermissionUtils
import com.mobiai.databinding.FragmentPermissionCallBinding

class CallPermisionFragment :BaseFragment<FragmentPermissionCallBinding>()
{
    companion object{
        fun instance() : CallPermisionFragment{
            return newInstance(CallPermisionFragment::class.java)
        }
    }
    private var goToSettingDialogPhone: GotosettingDialog? = null
    private var goToSettingDialogContact: GotosettingDialog? = null
    private var goToSettingDialogAudio: GotosettingDialog? = null

    private var isGotoSettingPhone = false
    private var isGotoSettingContact = false
    private var isGotoSettingAudio = false
    override fun initView() {
        checkPermission()
        binding.icClose.setOnSafeClickListener(500){
            handlerBackPressed()
        }
        binding.cvAllow.setOnSafeClickListener(1000){
            StoragePermissionUtils.requestPhonePermission(requestMultiplePhonePermissionsLauncher)
        }
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS
        )

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permission == Manifest.permission.READ_PHONE_STATE){
                    binding.icSelectPhone.visible()
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

        if (permission == Manifest.permission.READ_PHONE_STATE){
            isGotoSettingPhone = false
            if (ActivityCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED){
                binding.icSelectPhone.visible()
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    RxBus.publish(IsTurnOnCall())
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
                showGotoSettingPhoneDialog()
            }
        }

        else if (permission == Manifest.permission.READ_CONTACTS){
            isGotoSettingContact = false
            if (ActivityCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED) {
                binding.icSelectContact.visible()
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    RxBus.publish(IsTurnOnCall())
                    Handler().postDelayed({
                        handlerBackPressed()
                    },100)
                }
                else{
                    StoragePermissionUtils.requestContactPermission(requestMultipleAudioPermissionsLauncher)
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
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    RxBus.publish(IsTurnOnCall())
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
    private fun showGotoSettingPhoneDialog() {
        if (goToSettingDialogPhone==null){
            goToSettingDialogPhone = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                isGotoSettingPhone = true
                gotoSetting()
            }
        }
        if (!goToSettingDialogPhone!!.isShowing) {
            goToSettingDialogPhone!!.show()
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

    private val requestMultiplePhonePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo phone
            isGotoSettingPhone = false
            binding.icSelectPhone.visible()
            StoragePermissionUtils.requestContactPermission(requestMultipleContactPermissionsLauncher)
        } else {
            showGotoSettingPhoneDialog()
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
            SharedPreferenceUtils.isUnknownNumber = true
            SharedPreferenceUtils.isReadName = true
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
            SharedPreferenceUtils.isTurnOnModeNormal = true
            RxBus.publish(IsTurnOnCall())
            handlerBackPressed()
        } else {
            showGotoSettingAudioDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isGotoSettingPhone){
            checkPermissionOnResume(Manifest.permission.READ_PHONE_STATE)
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
    ): FragmentPermissionCallBinding = FragmentPermissionCallBinding.inflate(inflater,container,false)
}