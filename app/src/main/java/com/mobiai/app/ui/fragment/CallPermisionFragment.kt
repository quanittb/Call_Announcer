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
    private var goToSettingDialog: GotosettingDialog? = null
    override fun initView() {
        checkPermission()
        binding.icClose.setOnSafeClickListener(500){
            handlerBackPressed()
        }
        binding.cvAllow.setOnSafeClickListener(300){
            StoragePermissionUtils.requestPhonePermission(requestMultiplePhonePermissionsLauncher)
        }
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO
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
    private fun checkPermissionOnResume() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO
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
                    binding.icSelectPhone.visible()
                }
                if (permission == Manifest.permission.RECORD_AUDIO){
                    binding.icSelectPhone.visible()
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

    private val requestMultiplePhonePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo phone
            binding.icSelectPhone.visible()
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
            SharedPreferenceUtils.isUnknownNumber = true
            SharedPreferenceUtils.isReadName = true
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
            SharedPreferenceUtils.isTurnOnModeNormal = true
            RxBus.publish(IsTurnOnCall())
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
    ): FragmentPermissionCallBinding = FragmentPermissionCallBinding.inflate(inflater,container,false)
}