package com.mobiai.app.ui.fragment

import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ads.control.admob.Admob
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.facebook.shimmer.ShimmerFrameLayout
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.app.ultils.NetWorkChecker
import com.mobiai.app.ultils.NetworkConnected
import com.mobiai.app.ultils.listenEvent
import com.mobiai.base.basecode.extensions.gone
import com.mobiai.base.basecode.extensions.invisible
import com.mobiai.base.basecode.extensions.visible
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
    private fun initAdsNativeHome() {
        if (!AppPurchase.getInstance().isPurchased
            && AdsRemote.showNativeHome
            && App.getStorageCommon()?.nativeAdHome?.value == null
        ) {
            AperoAd.getInstance().loadNativeAdResultCallback(
                requireActivity(),
                BuildConfig.native_home,
                R.layout.layout_native_ads_onboarding,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        App.getStorageCommon()?.nativeAdHome?.postValue(nativeAd)
//                        isNativeAdsShowed = true
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        App.getStorageCommon()?.nativeAdHome?.postValue(null)
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
//                        isNativeAdsShowed = true
                    }

                }
            )
        } else {
            App.getStorageCommon()?.nativeAdHome?.postValue(App.getStorageCommon()?.nativeAdHome?.value)
//            isNativeAdsShowed = true
        }
    }

    private fun showNativeAdsHome() {
        if (AppPurchase.getInstance().isPurchased || !AdsRemote.showNativeHome) {
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
        }
    }

    private fun initAdsInterHome() {
        Log.i(
            TAG,
            "initAdsInterAddZodiac: isReady = ${App.getStorageCommon()?.mInterHome?.isReady}"
        )
        if (!AppPurchase.getInstance().isPurchased
            && AdsRemote.showInterHome
            && App.getStorageCommon()?.mInterHome == null
        ) {
            Log.i(TAG, "initAdsInterAddZodiac: compatibility fragment")
            AperoAd.getInstance().getInterstitialAds(
                requireContext(),
                BuildConfig.inter_home,
                object : AperoAdCallback() {
                    override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        App.getStorageCommon()?.mInterHome = interstitialAd
                    }
                }
            )
        }
    }

    private fun showInterHome(callback: () -> Unit) {
        var isNextActionEnable = true
        if (!AppPurchase.getInstance().isPurchased
            && AdsRemote.showInterHome
        ) {
            Admob.getInstance().setOpenActivityAfterShowInterAds(true)
            AperoAd.getInstance().forceShowInterstitial(
                requireContext(),
                App.getStorageCommon()?.mInterHome,
                object : AperoAdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        Admob.getInstance().setOpenActivityAfterShowInterAds(false)
                        if (isNextActionEnable) {
                            isNextActionEnable = false
                            callback()
                        }
                    }
                    override fun onAdFailedToShow(adError: ApAdError?) {
                        super.onAdFailedToShow(adError)
                        Admob.getInstance().setOpenActivityAfterShowInterAds(false)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        Admob.getInstance().setOpenActivityAfterShowInterAds(false)
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
                        if (!AppPurchase.getInstance().isPurchased && AdsRemote.showNativeHome) {
                            binding.flAds.visible()
                        }
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (isAdded) {
                                initAdsInterHome()
                                initAdsNativeHome()

                            }
                        }, 700)
                    }
                }
            }
        }))
    }
    override fun initView() {
        if (!NetWorkChecker.instance.isNetworkConnected(requireContext())){
            binding.flAds.gone()
        }
        initAdsInterHome()
        handlerEvent()
        showNativeAdsHome()
        binding.cvItemHomeCall.setOnSafeClickListener(500) {
            showInterHome {
                replaceFragment(CallAnnouncerFragment.instance())
            }
        }

        binding.cvItemHomeSMS.setOnSafeClickListener(500) {
            showInterHome {
                replaceFragment(SmsAnnouncerFragment.instance())
            }
        }

        binding.cvItemHomeSetting.setOnSafeClickListener(500) {
            replaceFragment(SettingFragment.instance())
        }

        //permissionSms()
      //  permissionDefault()
    }
    override fun onStop() {
        super.onStop()
        stopAds()
    }
    private fun stopAds() {
        binding.flAds.removeAllViews()
    }
    private fun resumeAds() {
        if (nativeAdHome != null && !AppPurchase.getInstance().isPurchased) {
            Log.d(TAG, "onResume: HOMEEE")
            AperoAd.getInstance().populateNativeAdView(
                requireActivity(),
                nativeAdHome,
                binding.flAds,
                binding.includeAdsNative.shimmerContainerNative as ShimmerFrameLayout
            )
        }
    }

    override fun onResume() {
        super.onResume()
        resumeAds()
    }
    private fun permissionSms(){
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.READ_SMS,android.Manifest.permission.SEND_SMS,android.Manifest.permission.RECEIVE_SMS), 111)
        }


    }
    private fun permissionDefault(){
      /*  // Kiểm tra xem quyền truy cập thông báo đã được cấp hay chưa
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            // Nếu chưa được cấp, yêu cầu quyền truy cập thông báo
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }*/

        // quyền truy cập vào không làm phiền
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.RECEIVE_BOOT_COMPLETED), 1)
        }

    }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container,false)
    }
}