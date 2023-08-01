package com.mobiai.app.ui.activity

import android.util.Log
import com.ads.control.admob.Admob
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.apero.inappupdate.AppUpdateManager
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.storage.FirebaseRemote
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.databinding.ActivitySplashBinding

class SplashActivity : BaseSplashActivity<ActivitySplashBinding>() {

    companion object {
        private const val TIME_OUT = 20000L
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

    private var adCallback: AperoAdCallback = object : AperoAdCallback() {
        override fun onNextAction() {
            super.onNextAction()
            openNextScreen()
            Log.e(TAG, "onNextAction: SPLASH_ACT")
        }
    }

    override fun startNotPurchase() {
        if(AdsRemote.showAdsSplash){
            Admob.getInstance().setOpenActivityAfterShowInterAds(false)
            AperoAd.getInstance().setInitCallback {
                AperoAd.getInstance()
                    .loadSplashInterstitialAds(
                        this,
                        BuildConfig.inter_splash,
                        20000,
                        2000,
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
            Log.i(TAG, "onAdClosed: SPLASH_ACT")
        }

        override fun onAdFailedToLoad(adError: ApAdError?) {
            super.onAdFailedToLoad(adError)
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();
            Log.i(TAG, "onAdFailedToLoad: SPLASH_ACT")
        }

        override fun onAdFailedToShow(adError: ApAdError?) {
            super.onAdFailedToShow(adError)
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();
            Log.i(TAG, "onAdFailedToShow: SPLASH_ACT")

        }

        override fun onNextAction() {
            super.onNextAction()
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();
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