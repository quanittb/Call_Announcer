package com.mobiai.app.ui.fragment

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.ads.control.admob.AppOpenManager
import com.mobiai.app.ui.dialog.GotosettingDialog
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.app.ultils.IsTurnOnCall
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.base.basecode.ultility.RxBus
import com.mobiai.app.ui.permission.StoragePermissionUtils
import com.mobiai.databinding.FragmentPermissionCallBinding

class CallPermisionFragment :BaseFragment<FragmentPermissionCallBinding>()
{
    companion object{
        fun instance() : CallPermisionFragment{
            return newInstance(CallPermisionFragment::class.java)
        }
    }
    private var goToSettingDialogCallerIDDialog: GotosettingDialog? = null
    private var goToSettingDialogContact: GotosettingDialog? = null
    private var goToSettingDialogPhoneState: GotosettingDialog? = null
    private var goToSettingDialogAudio: GotosettingDialog? = null

    private var isGotoSettingCallerID = false
    private var isGotoSettingContact = false
    private var isGotoSettingPhoneState = false
    private var isGotoSettingAudio = false
    private val CALL_SCREENING_REQUEST_CODE = 1111
    private lateinit var roleManager:RoleManager
    private val roleName = "android.app.role.CALL_SCREENING"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initView() {
        checkPermission()
        binding.icClose.setOnSafeClickListener(500){
            handlerBackPressed()
        }
        binding.cvAllow.setOnSafeClickListener(1000){
            if (!checkRoleStatus()){
                requestRoleStatus()
            }
            else{
               // ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_PHONE_STATE), 1111)
                StoragePermissionUtils.requestPhonePermission(requestMultiplePhoneStatePermissionsLauncher)
            }
        }
    }
    private fun requestRoleStatus() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
             val intent =  roleManager.createRequestRoleIntent("android.app.role.CALL_SCREENING")
             startActivityForResult(intent, CALL_SCREENING_REQUEST_CODE)
         }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkRoleStatus():Boolean {
         roleManager = requireContext().getSystemService(Context.ROLE_SERVICE) as RoleManager
        return roleManager.isRoleHeld(roleName)
    }


    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkRoleStatus()){
                binding.icSelectCallerID.visible()
            }
        }
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permission == Manifest.permission.READ_PHONE_STATE){
                    binding.icSelectPhoneState.visible()
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
            isGotoSettingPhoneState = false
            if (ActivityCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED){
                binding.icSelectPhoneState.visible()
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (checkRoleStatus()) {
                            RxBus.publish(IsTurnOnCall())
                            Handler().postDelayed({
                                handlerBackPressed()
                            },100)
                        }
                    }
                }

                else{
                    StoragePermissionUtils.requestContactPermission(requestMultipleContactPermissionsLauncher)
                }
            }
            else{
                checkPermission()
                showGotoSettingPhoneStateDialog()
            }
        }

        else
         if (permission == Manifest.permission.READ_CONTACTS){
            isGotoSettingContact = false
            if (ActivityCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED) {
                binding.icSelectContact.visible()
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (checkRoleStatus()) {
                            RxBus.publish(IsTurnOnCall())
                            Handler().postDelayed({
                                handlerBackPressed()
                            },100)
                        }
                    }
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
                if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (checkRoleStatus()) {
                            SharedPreferenceUtils.isTurnOnModeNormal = true
                            RxBus.publish(IsTurnOnCall())
                            Handler().postDelayed({
                                handlerBackPressed()
                            },100)
                        }
                    }
                }
            }
            else{
                checkPermission()
                showGotoSettingAudioDialog()
            }
        }
    }

    private fun showGotoSettingPhoneCallerIDDialog() {

        if (goToSettingDialogCallerIDDialog==null){
            goToSettingDialogCallerIDDialog = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                isGotoSettingCallerID = true
                val appSettingsIntent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                startActivity(appSettingsIntent)
            }
        }
        goToSettingDialogCallerIDDialog!!.gotoSetingCallerID()
        if (!goToSettingDialogCallerIDDialog!!.isShowing) {
            goToSettingDialogCallerIDDialog!!.show()
        }
    }
    private fun showGotoSettingPhoneStateDialog() {
        if (goToSettingDialogPhoneState==null){
            goToSettingDialogPhoneState = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                isGotoSettingPhoneState = true
                gotoSetting()
            }
        }
        goToSettingDialogPhoneState!!.gotoSetingCallState()
        if (!goToSettingDialogPhoneState!!.isShowing) {
            goToSettingDialogPhoneState!!.show()
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
        goToSettingDialogContact!!.gotoSetingContact()
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
        goToSettingDialogAudio!!.gotoSetingAudio()
        if (!goToSettingDialogAudio!!.isShowing) {
            goToSettingDialogAudio!!.show()
        }
    }

    private val requestMultiplePhoneStatePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo phone state
            isGotoSettingPhoneState = false
            binding.icSelectPhoneState.visible()
            StoragePermissionUtils.requestContactPermission(requestMultipleContactPermissionsLauncher)
        } else {
            showGotoSettingPhoneStateDialog()
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
            SharedPreferenceUtils.isTurnOnSmsNormal = true
            SharedPreferenceUtils.isTurnOnModeNormal = true
            RxBus.publish(IsTurnOnCall())
            handlerBackPressed()
        } else {
            showGotoSettingAudioDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isGotoSettingCallerID){
            isGotoSettingCallerID = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (checkRoleStatus()){
                    binding.icSelectCallerID.visible()
                    if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                        RxBus.publish(IsTurnOnCall())
                        Handler().postDelayed({
                            handlerBackPressed()
                        },100)
                    }
                    else{
                        //StoragePermissionUtils.requestContactPermission(requestMultipleContactPermissionsLauncher)
                        StoragePermissionUtils.requestPhonePermission(requestMultiplePhoneStatePermissionsLauncher)
                    }
                }
                else{
                    checkPermission()
                    showGotoSettingPhoneCallerIDDialog()
                }
            }
        }
        else if (isGotoSettingPhoneState){
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

    // Trong hàm onActivityResult
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CALL_SCREENING_REQUEST_CODE) {
            if (roleManager.isRoleHeld(roleName)) {
                StoragePermissionUtils.requestPhonePermission(requestMultiplePhoneStatePermissionsLauncher)
            } else {
                //todo goto setting caller ID
                showGotoSettingPhoneCallerIDDialog()
            }
        }
    }
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPermissionCallBinding = FragmentPermissionCallBinding.inflate(inflater,container,false)
}