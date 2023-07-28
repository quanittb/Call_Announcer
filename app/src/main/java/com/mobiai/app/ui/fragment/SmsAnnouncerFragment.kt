package com.mobiai.app.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.ads.control.admob.AppOpenManager
import com.mobiai.R
import com.mobiai.app.ui.dialog.GotosettingDialog
import com.mobiai.app.ui.dialog.TurnOnFlashDialog
import com.mobiai.app.ultils.IsTurnOnSms
import com.mobiai.app.ultils.listenEvent
import com.mobiai.base.basecode.extensions.gone
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.base_storage.permission.StoragePermissionUtils
import com.mobiai.databinding.FragmentSmsAnnouncerBinding

class SmsAnnouncerFragment :BaseFragment<FragmentSmsAnnouncerBinding>(){

    companion object{
        fun instance() : SmsAnnouncerFragment{
            return newInstance(SmsAnnouncerFragment::class.java)
        }
    }
    private var isFlashAvailable = false
    private var goToSettingDialog: GotosettingDialog? = null

    override fun initView() {
        checkStatus()

        isFlashAvailable =
            requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        checkPermission()

        binding.icBack.setOnClickListener {
            handlerBackPressed()
        }

        binding.btnTurn.setOnClickListener {
            turnOn()
        }
        binding.ivToggle1.setOnClickListener {
            changeToggle(binding.ivToggle1)
        }
        binding.ivToggle2.setOnClickListener {
            changeToggle(binding.ivToggle2)
        }

        binding.ivToggle3.setOnClickListener {
            changeToggle(binding.ivToggle3)
        }
        binding.ivToggle4.setOnClickListener {
            if (isFlashAvailable){
                changeToggle(binding.ivToggle4)
            }
            else{
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ){
                    showDialogTurnOn()

                }
                else{
                    changeToggle(binding.ivToggle4)
                }
            }
        }

        binding.ivToggle5.setOnClickListener {
            changeToggle(binding.ivToggle5)
        }

        binding.ivToggle6.setOnClickListener {
            changeToggle(binding.ivToggle6)
        }
        handlerEvent()
    }
    private val requestMultipleCameraPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            changeToggle(binding.ivToggle4)
        } else {
            showGotoSettingDialog()
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
    private fun turnOn(){
        if (!SharedPreferenceUtils.isTurnOnSms){
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
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Quyền không được cấp, xử lý tương ứng
                    addFragment(SmsPermisionFragment.instance())
                    return
                }
            }
            // full permission
            disableView(true)
            changeAllToggle(true)
            //showDialogTurnOn()
        }
        else{
            disableView(false)
            changeAllToggle(false)
        }
    }

    private fun handlerEvent() {
        addDispose(listenEvent({
            when (it) {
                is IsTurnOnSms -> {
                    disableView(true)
                    changeAllToggle(true)
                }
            }
        }))
    }

    private fun changeAllToggle(boolean: Boolean){
        if (boolean){
            binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            changeSharePreference(true)
        }
        else{
            binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            changeSharePreference(false)
        }
    }
    private fun changeSharePreference(boolean: Boolean){
        if (boolean){
            SharedPreferenceUtils.isTurnOnSmsNormal = true
            SharedPreferenceUtils.isTurnOnSmsSilent = false
            SharedPreferenceUtils.isTurnOnSmsVibrate = false
            SharedPreferenceUtils.isTurnOnFlashSms = false
            SharedPreferenceUtils.isUnknownNumberSms = true
            SharedPreferenceUtils.isReadNameSms = true
        }
        else{
            SharedPreferenceUtils.isTurnOnSmsNormal = false
            SharedPreferenceUtils.isTurnOnSmsSilent = false
            SharedPreferenceUtils.isTurnOnSmsVibrate = false
            SharedPreferenceUtils.isTurnOnFlashSms = false
            SharedPreferenceUtils.isUnknownNumberSms = false
            SharedPreferenceUtils.isReadNameSms = false
        }
    }
    private fun changeToggle(view: ImageView){
        when(view){
            binding.ivToggle1 -> {
                if (!SharedPreferenceUtils.isTurnOnSmsNormal){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    SharedPreferenceUtils.isTurnOnSmsNormal = true

                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    SharedPreferenceUtils.isTurnOnSmsNormal = false
                }
            }

            binding.ivToggle2 -> {
                if (!SharedPreferenceUtils.isTurnOnSmsSilent){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    SharedPreferenceUtils.isTurnOnSmsSilent = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    SharedPreferenceUtils.isTurnOnSmsSilent = false
                }
            }

            binding.ivToggle3 -> {
                if (!SharedPreferenceUtils.isTurnOnSmsVibrate){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    SharedPreferenceUtils.isTurnOnSmsVibrate = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    SharedPreferenceUtils.isTurnOnSmsVibrate = false
                }
            }
            binding.ivToggle4 -> {
                if (!SharedPreferenceUtils.isTurnOnFlashSms){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    SharedPreferenceUtils.isTurnOnFlashSms = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    SharedPreferenceUtils.isTurnOnFlashSms = false
                }
            }

            binding.ivToggle5 -> {
                if (!SharedPreferenceUtils.isUnknownNumberSms){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    SharedPreferenceUtils.isUnknownNumberSms = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    SharedPreferenceUtils.isUnknownNumberSms = false
                }
            }

            binding.ivToggle6 -> {
                if (!SharedPreferenceUtils.isReadNameSms){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    SharedPreferenceUtils.isReadNameSms = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    SharedPreferenceUtils.isReadNameSms = false
                }
            }
        }
     }

    private fun disableView(boolean: Boolean){
        if (!boolean){
            binding.frDisable.visible()
            binding.btnTurn.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on)
            binding.btnTurn.setTextColor(resources.getColor(R.color.color_text_turn))
            SharedPreferenceUtils.isTurnOnSms = false
        }
        else{
            binding.frDisable.gone()
            binding.btnTurn.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on_click)
            binding.btnTurn.setTextColor(resources.getColor(R.color.white))
            SharedPreferenceUtils.isTurnOnSms = true
        }

    }
    private fun checkToggle() {
        if (SharedPreferenceUtils.isTurnOnSmsNormal) {
            binding.ivToggle1.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle1.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }
        if (SharedPreferenceUtils.isTurnOnSmsSilent) {
            binding.ivToggle2.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle2.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }

        if (SharedPreferenceUtils.isTurnOnSmsVibrate) {
            binding.ivToggle3.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle3.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }

        if (SharedPreferenceUtils.isTurnOnFlashSms) {
            binding.ivToggle4.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle4.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }

        if (SharedPreferenceUtils.isUnknownNumberSms) {
            binding.ivToggle5.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle5.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }

        if (SharedPreferenceUtils.isReadNameSms) {
            binding.ivToggle6.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle6.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }
    }
    private fun checkStatus(){
        if (SharedPreferenceUtils.isTurnOnSms){
            disableView(true)
            checkToggle()
        }
        else{
            binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
        }
    }
    private fun changeOffToggle(view: ImageView){
        view.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_togle_off
            )
        )
    }
    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (permission == Manifest.permission.READ_SMS || permission == Manifest.permission.SEND_SMS || permission == Manifest.permission.RECEIVE_SMS){
                    SharedPreferenceUtils.isTurnOnSms = false
                    binding.btnTurn.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on)
                    binding.btnTurn.setTextColor(resources.getColor(R.color.color_text_turn))
                }

                if (permission == Manifest.permission.READ_CONTACTS){
                    SharedPreferenceUtils.isUnknownNumberSms = false
                    SharedPreferenceUtils.isReadNameSms = false
                    changeOffToggle(binding.ivToggle5)
                    changeOffToggle(binding.ivToggle6)
                }
                if (permission == Manifest.permission.RECORD_AUDIO){
                    SharedPreferenceUtils.isTurnOnSmsNormal = false
                    SharedPreferenceUtils.isTurnOnSmsVibrate = false
                    SharedPreferenceUtils.isTurnOnSmsSilent = false

                    changeOffToggle(binding.ivToggle1)
                    changeOffToggle(binding.ivToggle2)
                    changeOffToggle(binding.ivToggle3)

                }
                if (!isFlashAvailable && permission == Manifest.permission.CAMERA)
                {
                    SharedPreferenceUtils.isTurnOnFlashSms = false
                    changeOffToggle(binding.ivToggle4)
                }
            }
        }
    }
    private fun showDialogTurnOn(){
        val turnOnFlashDialog = TurnOnFlashDialog(requireContext()){
            StoragePermissionUtils.requestCameraPermission(requestMultipleCameraPermissionsLauncher)
        }
        turnOnFlashDialog.show()
    }
    override fun handlerBackPressed() {
        super.handlerBackPressed()
        closeFragment(this)
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSmsAnnouncerBinding = FragmentSmsAnnouncerBinding.inflate(inflater,container,false)
}