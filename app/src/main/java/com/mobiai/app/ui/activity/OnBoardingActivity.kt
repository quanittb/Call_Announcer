package com.mobiai.app.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
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
import com.mobiai.app.ultils.NetWorkChecker
import com.mobiai.base.basecode.ads.OnLoadNativeListener
import com.mobiai.base.basecode.ads.WrapAdsNative
import com.mobiai.base.basecode.extensions.invisible
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.transformation.ZoomOutSlideTransformation
import com.mobiai.base.basecode.ui.activity.BaseActivity
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.ActivityOnboardingBinding

class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding>() {
    private val TAG: String = OnBoardingActivity::javaClass.name
    private var fragmentList: ArrayList<BaseFragment<*>> = arrayListOf()
    private var lastPosition = 0
    var tempValue : MutableLiveData<ApNativeAd> =  MutableLiveData<ApNativeAd>()
    private var isLoadingFirstAds = false
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_onboarding
    }

    override fun getViewBinding(): ActivityOnboardingBinding {
        return ActivityOnboardingBinding.inflate(layoutInflater)
    }
    private fun initAds(){
        if(!NetWorkChecker.instance.isNetworkConnected(this) || !AdsRemote.showNativeOnboard)  binding.flAds.invisible()
        showNativeAdsOnBoarding()
        if ( App.getStorageCommon().nativeAdOnboard.value == null){
            isLoadingFirstAds = true
            loadAds(isFirstAds = true)
        }
        loadAds(isFirstAds = false)
    }
    override fun createView() {
        showFullscreen(true)
        initAds()
        fragmentList.add(OnBoardingFragment.newInstance(0))
        fragmentList.add(OnBoardingFragment.newInstance(1))
        fragmentList.add(OnBoardingFragment.newInstance(2))
        addControl()
        binding.tvNext.setOnClickListener {
            if (lastPosition != 2) {
                binding.viewPager.setCurrentItem(lastPosition + 1, true)
            } else {
                SharedPreferenceUtils.isCompleteOnboarding = true
                MainActivity.startMain(this, true)
            }
        }
    }
    override fun onNetworkAvailable() {
        runOnUiThread {
            if (!AppPurchase.getInstance().isPurchased && AdsRemote.showNativeOnboard && App.getStorageCommon().nativeAdOnboard.value == null && !isLoadingFirstAds){
                Log.i(TAG, "onNetworkAvailable: base")
                binding.flAds.visible()
                loadAds(true)
            }        }
        super.onNetworkAvailable()
    }

    private fun selectedView(view1: ImageView, view2: ImageView, view3: ImageView){
        view1.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_select_view_onboarding))
        view2.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_un_select_view))
        view3.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_un_select_view))
    }

    private fun addControl() {
        val adapter = OnBoardingViewPagerAdapter(
            supportFragmentManager,
            fragmentList,
            this
        )
        binding.viewPager.adapter = adapter
        selectedView(binding.icSelect1,binding.icSelect2,binding.icSelect3)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        selectedView(binding.icSelect1,binding.icSelect2,binding.icSelect3)
                    }
                    1 -> {
                        selectedView(binding.icSelect2,binding.icSelect3,binding.icSelect1)
                        binding.tvNext.text = getString(R.string.next)

                    }
                    else -> {
                        selectedView(binding.icSelect3,binding.icSelect2,binding.icSelect1)
                        binding.tvNext.text = getString(R.string._get_started)
                    }
                }

                if (lastPosition < position){
                    if (tempValue.value != null){
                        App.getStorageCommon().nativeAdOnboard.postValue(tempValue.value)
                    }
                    loadAds(false)
                }
                lastPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.setPageTransformer(true, ZoomOutSlideTransformation())
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

            if (lastPosition != 2) {
                binding.tvNext.text = getString(R.string.next)
            } else {
                binding.tvNext.text = getString(R.string._get_started)
            }
        }
        when (lastPosition) {
            0 -> {
                selectedView(binding.icSelect1,binding.icSelect2,binding.icSelect3)
            }
            1 -> {
                selectedView(binding.icSelect2,binding.icSelect3,binding.icSelect1)
                binding.tvNext.text = getString(R.string.next)

            }
            else -> {
                selectedView(binding.icSelect3,binding.icSelect2,binding.icSelect1)
                binding.tvNext.text = getString(R.string._get_started)
            }
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
        if (lastPosition != 0) {
            lastPosition--
            when (lastPosition) {
                0 -> {
                    selectedView(binding.icSelect1,binding.icSelect2,binding.icSelect3)
                }
                1 -> {
                    selectedView(binding.icSelect2,binding.icSelect3,binding.icSelect1)
                    binding.tvNext.text = getString(R.string.next)

                }
                else -> {
                    selectedView(binding.icSelect3,binding.icSelect2,binding.icSelect1)
                    binding.tvNext.text = getString(R.string._get_started)
                }
            }
            binding.tvNext.text = getString(R.string.next)
            binding.viewPager.currentItem = lastPosition
        } else {
            finish()
        }
    }
    private fun showNativeAdsOnBoarding() {
        if (AppPurchase.getInstance().isPurchased
            || !AdsRemote.showNativeOnboard )
        {
            binding.flAds.invisible()
        } else {
            App.getStorageCommon().nativeAdOnboard.observe(this) {
                if (it != null) {
                    AperoAd.getInstance().populateNativeAdView(
                        this,
                        it,
                        binding.flAds,
                        binding.includeAdsNative.shimmerContainerNative
                    )
                    binding.flAds.visible()
                } else {
                    binding.flAds.invisible()
                }
            }
        }
    }
    private fun loadAds(isFirstAds: Boolean) {
        WrapAdsNative(this,this, object : OnLoadNativeListener {
            override fun onLoadAdsFail() {
                if (isFirstAds){
                    isLoadingFirstAds = false
                    App.getStorageCommon().nativeAdOnboard.apply {
                        if (this.value != null){
                            Log.i(TAG, "onLoadAdsFail: checkvalueads ${this.value}")
                            postValue(this.value)
                        }
                    }
                }else{
                    tempValue.apply {
                        postValue(this.value)
                    }
                }

            }

            override fun onAdsImpression() {
            }

            override fun onLoadAdsFinish(nativeAd: ApNativeAd?) {
                if (isFirstAds){
                    isLoadingFirstAds = false
                    App.getStorageCommon().nativeAdOnboard.apply {
                        if (this.value == null){
                            Log.i(TAG, "onLoadAdsFinish: checkvalueads1 ${this.value}")
                            postValue(nativeAd)
                        }
                    }
                }else{
                    App.getStorageCommon().nativeAdOnboard.apply {
                        if (this.value == null){
                            Log.i(TAG, "onLoadAdsFinish: checkvalueads ${this.value}")
                            postValue(nativeAd)
                        }
                    }
                    tempValue.postValue(nativeAd)
                }
            }
        }, BuildConfig.native_onboard, R.layout.layout_native_ads_onboarding, AdsRemote.showNativeOnboard).initAdsNativeOld()

    }


}