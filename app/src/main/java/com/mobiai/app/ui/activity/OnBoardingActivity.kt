package com.mobiai.app.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.facebook.shimmer.ShimmerFrameLayout
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.adapter.OnBoardingViewPagerAdapter
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.ui.fragment.OnBoardingFragment
import com.mobiai.base.basecode.extensions.invisible
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.BaseActivity
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.ActivityOnboardingBinding

class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding>() {
    private val TAG: String = OnBoardingActivity::javaClass.name
    private var fragmentList: ArrayList<BaseFragment<*>> = arrayListOf()
    private var currentPosition = 0
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_onboarding
    }

    override fun getViewBinding(): ActivityOnboardingBinding {
        return ActivityOnboardingBinding.inflate(layoutInflater)
    }

    override fun createView() {
        showFullscreen(true)
        initAdsNative()
        fragmentList.add(OnBoardingFragment.newInstance(0))
        fragmentList.add(OnBoardingFragment.newInstance(1))
        fragmentList.add(OnBoardingFragment.newInstance(2))
        addControl()
        binding.tvNext.isEnabled = AppPurchase.getInstance().isPurchased
        if(!AdsRemote.showNativeOnboard ||  AppPurchase.getInstance().isPurchased){
            binding.tvNext.isEnabled = true
            binding.viewPager.setPagingEnabled(true)
            binding.flAds.invisible()
        }else{
            binding.tvNext.isEnabled = false
            binding.viewPager.setPagingEnabled(false)
        }
        binding.tvNext.isEnabled = false
        binding.viewPager.setPagingEnabled(false)

        binding.tvNext.setOnClickListener {
            if (currentPosition != 2) {
                binding.viewPager.currentItem = currentPosition + 1
            } else {
                SharedPreferenceUtils.isCompleteOnboarding = true
                MainActivity.startMain(this, true)
            }
        }
    }

    override fun onNetworkAvailable() {
        runOnUiThread {
            initAdsNative()
        }
        super.onNetworkAvailable()
    }

    private fun addControl() {
        val adapter = OnBoardingViewPagerAdapter(
            supportFragmentManager,
            fragmentList,
            this
        )
        binding.viewPager.adapter = adapter
        binding.wormDotsIndicator.attachTo(binding.viewPager)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                if (AppPurchase.getInstance().isPurchased || !AdsRemote.showNativeOnboard) {
                    currentPosition = position
                    binding.tvNext.isEnabled = true
                    binding.viewPager.setPagingEnabled(true)
                    if (currentPosition != 2) {
                        binding.tvNext.text = getString(R.string.next)
                    } else {
                        binding.tvNext.text = getString(R.string._get_started)
                    }

                } else {
                    if (positionOffset == 0f && currentPosition != position) {
                        showAdsNative(valueAdsNext)
                        currentPosition = position
                        binding.tvNext.isEnabled = false
                        binding.viewPager.setPagingEnabled(false)
                        if (!isFirstLoadAdsNative) {
                            initAdsNative()
                        }
                    }
                }
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.setPagingEnabled(true)
    }
    private var isFirstLoadAdsNative = true
    private var isLoadAdsNext =  false
    private var valueAdsNext  : ApNativeAd? = null
     private fun showAdsNative(nativeAd: ApNativeAd?){
        Log.d(TAG, "showAdsNative: Onboarding  ${nativeAd}")
        if(nativeAd != null){
            binding.flAds.visible()
            Handler(Looper.getMainLooper()).postDelayed({
                AperoAd.getInstance().populateNativeAdView(this@OnBoardingActivity, nativeAd, binding.flAds, binding.includeAdsNative.shimmerContainerNative as ShimmerFrameLayout)
            }, 600)
        } else{
            binding.flAds.invisible()
        }

    }

    private fun handleLoadAdsComplete(nativeAd: ApNativeAd?) {
        if (isFirstLoadAdsNative) {
            Log.d(TAG, "handleLoadAdsComplete: onboard $isFirstLoadAdsNative")
            showAdsNative(nativeAd)
            binding.tvNext.text = getString(R.string.next)
        } else {

            binding.tvNext.isEnabled = true
            binding.viewPager.setPagingEnabled(true)
            if (currentPosition != 2) {
                binding.tvNext.text = getString(R.string.next)
            } else {
                binding.tvNext.text = getString(R.string._get_started)
            }
        }

    }

    private fun handleLoadAdsFail() {

        if (isFirstLoadAdsNative) {
            binding.flAds.invisible()
            initAdsNative()
            isFirstLoadAdsNative = false
            isLoadAdsNext = true
        }
        binding.viewPager.setPagingEnabled(true)
        binding.tvNext.isEnabled = true
        if (currentPosition != 2) {
            binding.tvNext.text = getString(R.string.next)
        } else {
            binding.tvNext.text = getString(R.string._get_started)
        }

    }

    private fun handleAdsImpression() {
        if (isFirstLoadAdsNative) {
            initAdsNative()
            isFirstLoadAdsNative = false
            isLoadAdsNext = true
        }
    }

    fun initAdsNative() {
        if (!AppPurchase.getInstance().isPurchased && AdsRemote.showNativeOnboard) {
            AperoAd.getInstance().loadNativeAdResultCallback(
                this,
                BuildConfig.native_onboard,
                R.layout.layout_native_ads_onboarding,
                object : AperoAdCallback() {
                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        //    App.getStorageCommon()?.nativeAdOnboard?.postValue(null)
                        handleLoadAdsFail()
                        Log.e(TAG, "onAdFailedToLoad: onboard", )
                    }

                    override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        // App.getStorageCommon()?.nativeAdOnboard?.postValue(nativeAd)
                        if(isLoadAdsNext){
                            valueAdsNext = nativeAd
                        }
                        handleLoadAdsComplete(nativeAd)
                        Log.e(TAG, "onNativeAdLoaded: onboard", )
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        handleAdsImpression()
                        Log.e(TAG, "onAdImpression: onboard", )
                    }
                })
        }else{
            App.getStorageCommon()?.nativeAdOnboard?.postValue(App.getStorageCommon()?.nativeAdOnboard?.value)
        }
    }
    companion object {
        fun start(context: Context, clearTask: Boolean = true) {
            Intent(context, OnBoardingActivity::class.java).apply {
                if (clearTask) {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(this)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currentPosition != 0) {
            currentPosition--
            binding.tvNext.text = getString(R.string.next)
            binding.viewPager.currentItem = currentPosition
        } else {
            finish()
        }
    }

}