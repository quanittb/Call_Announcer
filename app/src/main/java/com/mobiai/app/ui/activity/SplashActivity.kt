package com.mobiai.app.ui.activity

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.ads.control.admob.Admob
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.apero.inappupdate.AppUpdateManager
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.storage.FirebaseRemote
import com.mobiai.app.ultils.NetWorkChecker
import com.mobiai.app.ultils.NotificationPermission
import com.mobiai.base.basecode.ads.OnLoadNativeListener
import com.mobiai.base.basecode.ads.WrapAdsNative
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.BaseActivity.Companion.initAdsNativeHome
import com.mobiai.base.basecode.ui.activity.BaseActivity.Companion.initAdsNativeLanguage
import com.mobiai.databinding.ActivitySplashBinding

class SplashActivity : BaseSplashActivity<ActivitySplashBinding>() {

    companion object {
        private const val TIME_OUT = 30000L
        private const val TIME_DELAY = 2000L

        private val TAG = SplashActivity::class.java.name
    }

    override fun getIdAdsSplash(): String = BuildConfig.inter_splash

    override fun getIdNativeLanguage(): String = BuildConfig.native_language


    override fun getLayoutResourceId(): Int = R.layout.activity_splash

    override fun getViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)

    override fun createView() {
        configUpdate()
    }

    override fun actionLoadAdsSplashWhenFetchData() {
        super.actionLoadAdsSplashWhenFetchData()
        setUpPurchase()
        initAdsNativeLanguage(this)
        initAdsNativeHome(this)
    }

    override fun openNextScreen() {
        if ( !SharedPreferenceUtils.isCompleteOnboarding && AdsRemote.showNativeOnboard) {
            WrapAdsNative(this,this, object : OnLoadNativeListener {
                override fun onLoadAdsFail() {
                }

                override fun onAdsImpression() {
                }

                override fun onLoadAdsFinish(nativeAd: ApNativeAd?) {
                    App.getStorageCommon().nativeAdOnboard.apply {
                        if (this.value == null){
                            Log.i(TAG, "onLoadAdsFinish: splash checkvalueads ${this.value}")
                            postValue(nativeAd)
                        }
                    }
                }
            }, BuildConfig.native_onboard, R.layout.layout_native_ads_onboarding, AdsRemote.showNativeOnboard).initAdsNativeOld()
        }
        if (SharedPreferenceUtils.languageCode == null) {
            LanguageActivity.start(this, clearTask = true)
        }
        else if(!SharedPreferenceUtils.isCompleteOnboarding){
            LanguageUtil.changeLang(SharedPreferenceUtils.languageCode!!, this)
            OnBoardingActivity.start(this, true)
        } else{
            LanguageUtil.changeLang(SharedPreferenceUtils.languageCode!!, this)
            MainActivity.startMain(this, true)
        }
    }

    private fun configUpdate() {
        if (intent.data == null) {
            FirebaseRemote.keyUpdateState?.let {
                AppUpdateManager.getInstance(this).setupUpdate(it, FirebaseRemote.showUpdateTimes)
            }
        } else {
            AppUpdateManager.getInstance(this).isStartSessionFromOtherApp = true
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")

    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private var adCallback: AperoAdCallback = object : AperoAdCallback() {
        override fun onNextAction() {
            super.onNextAction()
            openNextScreen()
            Admob.getInstance().setOpenActivityAfterShowInterAds(true)
            Log.e(TAG, "onNextAction qqqqqq: SPLASH_ACT")
        }
    }

    override fun startNotPurchase() {
        if(AdsRemote.showAdsSplash){
            Admob.getInstance().setOpenActivityAfterShowInterAds(true)
            AperoAd.getInstance().setInitCallback {
                AperoAd.getInstance()
                    .loadSplashInterstitialAds(
                        this,
                        BuildConfig.inter_splash,
                        30000,
                        5000,
                        true,
                        adAperoCallBack
                    )
            }
        }else{
            openNextScreen()
        }
    }


    private var adAperoCallBack = object : AperoAdCallback() {
        override fun onAdClosed() {
            super.onAdClosed()
            if (isDestroyed || isFinishing || isOnStop) return

            openNextScreen();
            Admob.getInstance().setOpenActivityAfterShowInterAds(true)
            Log.i(TAG, "onAdClosed: SPLASH_ACT")
        }

        override fun onAdFailedToLoad(adError: ApAdError?) {
            super.onAdFailedToLoad(adError)
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();
            Admob.getInstance().setOpenActivityAfterShowInterAds(true)
            Log.i(TAG, "onAdFailedToLoad: SPLASH_ACT")
        }

        override fun onAdFailedToShow(adError: ApAdError?) {
            super.onAdFailedToShow(adError)
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();
            Admob.getInstance().setOpenActivityAfterShowInterAds(true)
            Log.i(TAG, "onAdFailedToShow: SPLASH_ACT")

        }

        override fun onNextAction() {
            super.onNextAction()
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();
            Admob.getInstance().setOpenActivityAfterShowInterAds(true)
            Log.i(TAG, "onNextAction: SPLASH_ACT")
        }

    }

    override fun checkShowSplashFail() {
        if (AdsRemote.showAdsSplash) {
            Log.i(TAG, "checkShowSplashFail: SPLASH_ACT")
            AperoAd.getInstance().onCheckShowSplashWhenFail(this, adCallback, TIME_DELAY.toInt())
        }
    }

}