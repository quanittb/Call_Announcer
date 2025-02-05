package com.mobiai.app.ui.fragment

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.facebook.shimmer.ShimmerFrameLayout
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.ui.dialog.GotosettingDialog
import com.mobiai.app.ui.dialog.TurnOnFlashDialog
import com.mobiai.app.ultils.IsTurnOnCall
import com.mobiai.app.ultils.NetWorkChecker
import com.mobiai.app.ultils.NetworkConnected
import com.mobiai.app.ultils.listenEvent
import com.mobiai.base.basecode.extensions.gone
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.app.ui.permission.StoragePermissionUtils
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.databinding.FragmentCallAnnouncerBinding

class CallAnnouncerFragment :BaseFragment<FragmentCallAnnouncerBinding>(){

    companion object{
        fun instance() : CallAnnouncerFragment{
            return newInstance(CallAnnouncerFragment::class.java)
        }
    }
    private var isFlashAvailable = false
    private var goToSettingDialog: GotosettingDialog? = null
    private lateinit var roleManager:RoleManager
    private val roleName = "android.app.role.CALL_SCREENING"

    private var isNativeAdsInit: Boolean = false
    private fun initAdsNativeCall() {
        if (!AppPurchase.getInstance().isPurchased
            && AdsRemote.showNativeCall
            && App.getStorageCommon()?.nativeAdCall?.value == null
        ) {
            AperoAd.getInstance().loadNativeAdResultCallback(
                requireActivity(),
                BuildConfig.native_call,
                R.layout.layout_native_ads_call,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        App.getStorageCommon()?.nativeAdCall?.postValue(nativeAd)
                        isNativeAdsInit = true
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        isNativeAdsInit = true
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        App.getStorageCommon()?.nativeAdCall?.postValue(null)
                    }
                }
            )
        } else {
            App.getStorageCommon()?.nativeAdCall?.postValue(App.getStorageCommon()?.nativeAdCall?.value)
            isNativeAdsInit = true

        }
    }

    private fun showAdsCall() {
        if (AppPurchase.getInstance().isPurchased || !AdsRemote.showNativeCall) {
            binding.flAds.gone()
        } else {
            App.getStorageCommon()?.nativeAdCall?.observe(this) {
                if (it != null) {
                    AperoAd.getInstance().populateNativeAdView(
                        requireActivity(),
                        it,
                        binding.flAds,
                        binding.includeAdsNative.shimmerContainerNative as ShimmerFrameLayout
                    )
                } else {
                    binding.flAds.gone()
                }
            }
        }
    }

    private fun handlerEvent() {
        addDispose(listenEvent({
            when (it) {
                is IsTurnOnCall -> {
                    disableView(true)
                    changeAllToggle(true)
                 //   createService()
                }
                is NetworkConnected -> {
                    if (it.isOn) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (!AppPurchase.getInstance().isPurchased && AdsRemote.showNativeCall){
                                binding.flAds.visible()
                            }
                            if (isAdded){
                                if (!isNativeAdsInit) {
                                    initAdsNativeCall()
                                }
                            }
                        }, 700)

                    }else{
                        if (!isNativeAdsInit){
                            binding.flAds.gone()
                        }
                    }
                }
            }
        }))
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initView() {
        showAdsCall()
        checkStatus()
        isFlashAvailable =
            requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        if (SharedPreferenceUtils.isTurnOnCall){
            checkPermission()
        }

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
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkRoleStatus():Boolean {
        roleManager = requireContext().getSystemService(Context.ROLE_SERVICE) as RoleManager
        return roleManager.isRoleHeld(roleName)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (!checkRoleStatus()){
            permissionDenied()
            return
        }
        else{
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (permission == Manifest.permission.READ_PHONE_STATE){
                        permissionDenied()
                        return
                    }
                    else{
                        if (permission == Manifest.permission.READ_CONTACTS){
                            SharedPreferenceUtils.isUnknownNumber = false
                            SharedPreferenceUtils.isReadName = false
                            changeOffToggle(binding.ivToggle5)
                            changeOffToggle(binding.ivToggle6)
                            binding.txtName.text = getString(R.string.announce_phone_number_in_contacts)
                            permissionDenied()
                        }
                        if (permission == Manifest.permission.RECORD_AUDIO){
                            SharedPreferenceUtils.isTurnOnModeNormal = false
                            SharedPreferenceUtils.isTurnOnModeVibrate = false
                            SharedPreferenceUtils.isTurnOnModeSilent = false
                            changeOffToggle(binding.ivToggle1)
                            changeOffToggle(binding.ivToggle2)
                            changeOffToggle(binding.ivToggle3)
                            permissionDenied()

                        }
                        if (!isFlashAvailable && permission == Manifest.permission.CAMERA)
                        {
                            SharedPreferenceUtils.isTurnOnFlash = false
                            changeOffToggle(binding.ivToggle4)
                            permissionDenied()
                        }


                    }
                }
            }
        }
    }

    private fun permissionDenied(){
        SharedPreferenceUtils.isTurnOnCall = false
        binding.btnTurn.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on)
        binding.btnTurn.setTextColor(resources.getColor(R.color.color_text_turn))
        binding.btnTurn.text = (resources.getString(R.string.turn_on))
        checkStatusResumeOff()
    }

    private fun changeOffToggle(view: ImageView){
        view.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_togle_off
            )
        )
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

    private fun checkStatus(){
        if (SharedPreferenceUtils.isTurnOnCall){
            disableView(true)
            checkToggle()
        }
        else{
            disableView(false)
            binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.txtName.text = getString(R.string.announce_phone_number_in_contacts)
        }
    }

    private fun checkStatusResumeOff(){
        disableView(false)
        binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
        binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
        binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
        binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
        binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
        binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
        binding.txtName.text = getString(R.string.announce_phone_number_in_contacts)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        SharedPreferenceUtils.languageCode?.let { LanguageUtil.changeLang(it, requireContext()) }
        if (SharedPreferenceUtils.isTurnOnCall){
            checkPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun turnOn() {
        if (!SharedPreferenceUtils.isTurnOnCall) {
            val permissions = arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO
            )
            if (!checkRoleStatus()){
                // Quyền không được cấp, xử lý tương ứng
                addFragment(CallPermisionFragment.instance())
                return
            }
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Quyền không được cấp, xử lý tương ứng
                    addFragment(CallPermisionFragment.instance())
                    return
                }
            }
            // full permission
            disableView(true)
            changeAllToggle(true)
        } else {
            disableView(false)
            changeAllToggle(false)
        }
    }

    private fun disableView(boolean: Boolean) {
        if (!boolean) {
            binding.btnTurn.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on)
            binding.btnTurn.setTextColor(resources.getColor(R.color.color_text_turn))
            binding.btnTurn.text = (resources.getString(R.string.turn_on))
            SharedPreferenceUtils.isTurnOnCall = false
            binding.ivToggle1.isEnabled = false
            binding.ivToggle2.isEnabled = false
            binding.ivToggle3.isEnabled = false
            binding.ivToggle4.isEnabled = false
            binding.ivToggle5.isEnabled = false
            binding.ivToggle6.isEnabled = false
        } else {
            binding.btnTurn.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on_click)
            binding.btnTurn.setTextColor(resources.getColor(R.color.white))
            binding.btnTurn.text = (resources.getString(R.string.turn_off))
            SharedPreferenceUtils.isTurnOnCall = true
            binding.ivToggle1.isEnabled = true
            binding.ivToggle2.isEnabled = true
            binding.ivToggle3.isEnabled = true
            binding.ivToggle4.isEnabled = true
            binding.ivToggle5.isEnabled = true
            binding.ivToggle6.isEnabled = true
        }
    }

    private fun changeAllToggle(boolean: Boolean){
        if (boolean){
            if (SharedPreferenceUtils.isTurnOnModeNormal){
                binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            }
            else{
                binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            }
            if (SharedPreferenceUtils.isTurnOnModeSilent)
            {
                binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            }
            else{
                binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            }
            if (SharedPreferenceUtils.isTurnOnModeVibrate){
                binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            }
            else{
                binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            }
            if (SharedPreferenceUtils.isTurnOnFlash){
                binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            }
            else{
                binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            }
            if (SharedPreferenceUtils.isUnknownNumber){
                binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            }
            else{
                binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            }
            if (SharedPreferenceUtils.isReadName){
                binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                binding.txtName.text = getString(R.string.announce_name_in_contacts)
            }
           else{
                binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                binding.txtName.text = getString(R.string.announce_phone_number_in_contacts)
            }

           if (!SharedPreferenceUtils.isTurnOnModeNormal && !SharedPreferenceUtils.isTurnOnModeSilent && !SharedPreferenceUtils.isTurnOnModeVibrate &&
               !SharedPreferenceUtils.isTurnOnFlash && !SharedPreferenceUtils.isUnknownNumber && !SharedPreferenceUtils.isReadName){
                binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                binding.txtName.text = getString(R.string.announce_name_in_contacts)
                changeSharePreference(true)
            }
        }
        else{
            binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.txtName.text = getString(R.string.announce_phone_number_in_contacts)
            //changeSharePreference(false)
        }
    }

    private fun changeSharePreference(boolean: Boolean){
        if (boolean){
            SharedPreferenceUtils.isTurnOnModeNormal = true
            SharedPreferenceUtils.isTurnOnModeSilent = false
            SharedPreferenceUtils.isTurnOnModeVibrate = false
            SharedPreferenceUtils.isTurnOnFlash = false
            SharedPreferenceUtils.isUnknownNumber = true
            SharedPreferenceUtils.isReadName = true
        }
        else{
            SharedPreferenceUtils.isTurnOnModeNormal = false
            SharedPreferenceUtils.isTurnOnModeSilent = false
            SharedPreferenceUtils.isTurnOnModeVibrate = false
            SharedPreferenceUtils.isTurnOnFlash = false
            SharedPreferenceUtils.isUnknownNumber = false
            SharedPreferenceUtils.isReadName = false
        }
    }

    private fun changeToggle(view: ImageView) {
        when (view) {
            binding.ivToggle1 -> {
                if (!SharedPreferenceUtils.isTurnOnModeNormal) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeNormal = true
                }
                else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeNormal = false

                }
            }

            binding.ivToggle2 -> {
                if (!SharedPreferenceUtils.isTurnOnModeSilent) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeSilent = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeSilent = false
                }
            }

            binding.ivToggle3 -> {
                if (!SharedPreferenceUtils.isTurnOnModeVibrate) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeVibrate = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeVibrate = false
                }
            }

            binding.ivToggle4 -> {
                if (!SharedPreferenceUtils.isTurnOnFlash) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isTurnOnFlash = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isTurnOnFlash = false
                }
            }

            binding.ivToggle5 -> {
                if (!SharedPreferenceUtils.isUnknownNumber) {

                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isUnknownNumber = true
                } else {

                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isUnknownNumber = false
                }
            }

            binding.ivToggle6 -> {
                if (!SharedPreferenceUtils.isReadName) {
                    binding.txtName.text = getString(R.string.announce_name_in_contacts)
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isReadName = true
                } else {
                    binding.txtName.text = getString(R.string.announce_phone_number_in_contacts)
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isReadName = false
                }
            }
        }
    }
    private fun checkToggle() {
        if (SharedPreferenceUtils.isTurnOnModeNormal) {
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
        if (SharedPreferenceUtils.isTurnOnModeSilent) {
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

        if (SharedPreferenceUtils.isTurnOnModeVibrate) {
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

        if (SharedPreferenceUtils.isTurnOnFlash) {
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

        if (SharedPreferenceUtils.isUnknownNumber) {
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

        if (SharedPreferenceUtils.isReadName) {
            binding.txtName.text = getString(R.string.announce_name_in_contacts)
            binding.ivToggle6.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.txtName.text = getString(R.string.announce_phone_number_in_contacts)
            binding.ivToggle6.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
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

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCallAnnouncerBinding = FragmentCallAnnouncerBinding.inflate(inflater,container,false)
}