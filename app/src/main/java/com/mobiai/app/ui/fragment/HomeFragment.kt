package com.mobiai.app.ui.fragment

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.ads.control.admob.Admob
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.ads.control.funtion.AdCallback
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.storage.FirebaseRemote
import com.mobiai.app.ui.activity.CallActivity
import com.mobiai.app.ui.activity.SMSActivity
import com.mobiai.app.ui.activity.SettingActivity
import com.mobiai.app.ui.dialog.GotosettingDialog
import com.mobiai.app.ui.permission.StoragePermissionUtils
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.app.ultils.NetWorkChecker
import com.mobiai.app.ultils.NetworkConnected
import com.mobiai.app.ultils.listenEvent
import com.mobiai.base.basecode.extensions.gone
import com.mobiai.base.basecode.extensions.invisible
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    companion object{
        fun instance() : HomeFragment{
            return newInstance(HomeFragment::class.java)
        }
    }
    private val TAG = HomeFragment::javaClass.name
    private var nativeAdHome: ApNativeAd? = null
    private var isNativeAdsShowed = false
    private var isGotoSettingNotify = false
    private var goToSettingDialogNotify: GotosettingDialog? = null
    private var isBannerShowed: Boolean = false
    private var isInitingBanner: Boolean = false
    private fun initAdsNativeHome() {
        if (!AppPurchase.getInstance().isPurchased
            && AdsRemote.showNativeHome
            && App.getStorageCommon()?.nativeAdHome?.value == null
        ) {
            AperoAd.getInstance().loadNativeAdResultCallback(
                requireActivity(),
                BuildConfig.native_home,
                R.layout.layout_native_ads_home,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        App.getStorageCommon()?.nativeAdHome?.postValue(nativeAd)
                        isNativeAdsShowed = true
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        App.getStorageCommon()?.nativeAdHome?.postValue(null)
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        isNativeAdsShowed = true
                    }

                }
            )
        } else {
            App.getStorageCommon()?.nativeAdHome?.postValue(App.getStorageCommon()?.nativeAdHome?.value)
               isNativeAdsShowed = true
        }
    }


    private fun showNativeAdsHome() {
      /*  if (AppPurchase.getInstance().isPurchased || !AdsRemote.showNativeHome) {
            binding.flAds.invisible()
        } else {
            App.getStorageCommon()?.nativeAdHome?.observe(this) {
                if (it != null) {
                    AperoAd.getInstance().populateNativeAdView(
                        requireActivity(),
                        it,
                        binding.flAds,
                        binding.includeAdsNative.shimmerContainerNative as ShimmerFrameLayout
                    )
                    nativeAdHome = it
                } else {
                    binding.flAds.invisible()
                }
            }
        }*/
    }

    private fun initAdsCollapsibleBanner() {
        if (isInitingBanner) return
        if (!AppPurchase.getInstance().isPurchased && AdsRemote.showCollapsibleBanner) {
            isInitingBanner = true
            AperoAd.getInstance().loadCollapsibleBanner(
                requireActivity(),
                BuildConfig.collapsiblebanner_home,
                "bottom",
                object : AdCallback() {
                    override fun onAdImpression() {
                        super.onAdImpression()
                        Log.e(TAG, "onAdImpression: BottomFragment")
                        isBannerShowed = true
                        isInitingBanner = false
                    }

                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        isInitingBanner = false
                        isBannerShowed = false
                        Log.e(TAG, "onAdFailedToLoad: BottomFragment", )
                    }

                    override fun onAdFailedToShow(adError: AdError?) {
                        super.onAdFailedToShow(adError)
                        Log.e(TAG, "onAdFailedToShow: BottomFragment", )
                        isInitingBanner = false
                        isBannerShowed = false
                        binding.lineSpaceAds.invisible()
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        isInitingBanner = false
                        isBannerShowed = false
                        Log.e(TAG, "onAdClosed: BottomFragment", )

                    }

                    override fun onAdLeftApplication() {
                        super.onAdLeftApplication()
                        Log.e(TAG, "onAdLeftApplication: BottomFragment ", )
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        isBannerShowed = true
                        isInitingBanner = false
                        Log.e(TAG, "onAdLoaded: BottomFragment", )
                    }
                })
        } else {
            isInitingBanner = false
            binding.flAds.gone()
            binding.lineSpaceAds.gone()
        }
    }


    private fun showInterHome(callback: () -> Unit) {
        var isNextActionEnable = true
        if (!AppPurchase.getInstance().isPurchased
            && AdsRemote.showInterHome
        ) {
           // Admob.getInstance().setOpenActivityAfterShowInterAds(true)
            AperoAd.getInstance().forceShowInterstitial(
                requireContext(),
                App.getStorageCommon()?.mInterHome,
                object : AperoAdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        //Admob.getInstance().setOpenActivityAfterShowInterAds(false)
                        if (isNextActionEnable) {
                            isNextActionEnable = false
                            callback()
                        }
                    }
                    override fun onAdFailedToShow(adError: ApAdError?) {
                        super.onAdFailedToShow(adError)
                       // Admob.getInstance().setOpenActivityAfterShowInterAds(false)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                      //  Admob.getInstance().setOpenActivityAfterShowInterAds(false)
                    }
                    override fun onAdClosed() {
                        super.onAdClosed()
                      //  Admob.getInstance().setOpenActivityAfterShowInterAds(false)

                    }
                    override fun onAdImpression() {
                        super.onAdImpression()
                      //  Admob.getInstance().setOpenActivityAfterShowInterAds(false)
                    }

                }, true
            )
        } else {
            callback()
        }
    }

    private fun handlerEvent() {
        addDispose(listenEvent({
            when (it) {
                is NetworkConnected -> {
                    if (it.isOn) {
                            if (!AppPurchase.getInstance().isPurchased && AdsRemote.showCollapsibleBanner){
                                binding.flAds.visible()
                            }
                        Handler(Looper.getMainLooper()).postDelayed({
                        if (isAdded){
                               /* if (!isNativeAdsShowed) {
                                    initAdsNativeHome()
                                }*/
                                if (!isBannerShowed) {
                                    Log.e(TAG, "handlerEvent: initBaner BottomFragment", )
                                    initAdsCollapsibleBanner()
                                }
                            }
                        }, 700)

                    }else{
                       /* if (!isNativeAdsShowed){
                            binding.flAds.gone()
                        }*/
                        if (!isBannerShowed) {
                            Log.i(TAG, "handlerEvent: aaaaaaaaaaaaaaaaaa")
                            binding.flAds.gone()
                            binding.lineSpaceAds.gone()
                        }
                    }
                }
            }
        }))
    }
    private fun showGotoSettingNotifyDialog() {
        if (goToSettingDialogNotify==null){
            goToSettingDialogNotify = GotosettingDialog(
                requireContext(),
            ) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                isGotoSettingNotify = true
                gotoSetting()
            }
        }
        if (!goToSettingDialogNotify!!.isShowing) {
            goToSettingDialogNotify!!.show()
        }
    }
    private val requestMultipleNotifyPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            //todo phone call log
            isGotoSettingNotify = false
        } else {
            showGotoSettingNotifyDialog()
        }
        SharedPreferenceUtils.isPermission = true

    }
    override fun initView() {
        initAds()
        if (!SharedPreferenceUtils.isPermission && StoragePermissionUtils.isAPI33OrHigher()){
            StoragePermissionUtils.requestNotifyPermission(requestMultipleNotifyPermissionsLauncher)
        }
        binding.cvItemHomeCall.setOnSafeClickListener(500) {
            showInterHome {
                CallActivity.start(requireContext())
            }
        }

        binding.cvItemHomeSMS.setOnSafeClickListener(500) {
            showInterHome {
                SMSActivity.start(requireContext())
            }
        }

        binding.cvItemHomeSetting.setOnSafeClickListener(500) {
                SettingActivity.start(requireContext())
        }
        handlerEvent()
    }

    private fun initAds() {
        /* if (!NetWorkChecker.instance.isNetworkConnected(requireContext())){
           binding.flAds.gone()
       }
       showNativeAdsHome()*/
        if (!AppPurchase.getInstance().isPurchased && NetWorkChecker.instance.isNetworkConnected(requireContext()) && AdsRemote.showCollapsibleBanner && !isBannerShowed) {
            binding.flAds.visible()
            binding.lineSpaceAds.visible()
            initAdsCollapsibleBanner()
        }
    }

    override fun onStop() {
        super.onStop()
        //stopAds()
    }
    private fun stopAds() {
        binding.flAds.removeAllViews()
    }
    private fun resumeAds() {
        /*if (nativeAdHome != null && !AppPurchase.getInstance().isPurchased) {
            Log.d(TAG, "onResume: HOMEEE")
            AperoAd.getInstance().populateNativeAdView(
                requireActivity(),
                nativeAdHome,
                binding.flAds,
                binding.includeAdsNative.shimmerContainerNative as ShimmerFrameLayout
            )
        }*/
    }

    override fun onResume() {
        super.onResume()
        SharedPreferenceUtils.languageCode?.let { LanguageUtil.changeLang(it, requireContext()) }
       // resumeAds()
           /* if (!AppPurchase.getInstance().isPurchased && AdsRemote.showNativeCall){
                binding.flAds.visible()
            }
            if (isAdded){
                if (!isNativeAdsShowed) {
                    initAdsNativeHome()
                }
            }*/
    }

    override fun handlerBackPressed() {
        super.handlerBackPressed()
        requireActivity().finish()
    }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container,false)
    }
}