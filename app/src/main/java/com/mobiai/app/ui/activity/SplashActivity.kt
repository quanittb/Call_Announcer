package com.mobiai.app.ui.activity

import android.util.Log
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.apero.inappupdate.AppUpdateManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.storage.FirebaseRemote
import com.mobiai.base.basecode.ads.WrapAdsResume
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.splash.BaseSplashActivity
import com.mobiai.databinding.ActivitySplashBinding

class SplashActivity(override val isShowAdsSplash: Boolean = AdsRemote.showAdsSplash, override val idAdsNormal: String = BuildConfig.inter_splash) : BaseSplashActivity<ActivitySplashBinding>() {

    override fun getLayoutResourceId(): Int  = R.layout.activity_splash

    override fun getViewBinding(): ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
    override fun createView() {

    }

    override fun openNextScreen() {
        if (SharedPreferenceUtils.languageCode == null) {
            LanguageActivity.start(this, clearTask = true)
        }
        else if(!SharedPreferenceUtils.isCompleteOnboarding){
            LanguageUtil.changeLang(SharedPreferenceUtils.languageCode!!, this)
            OnBoardingActivity.start(this, true)
        }
        else {
            LanguageUtil.changeLang(SharedPreferenceUtils.languageCode!!, this)
            MainActivity.startMain(this, true)
        }
    }

    override fun startNotPurchase() {
        if(AdsRemote.showAdsSplash){
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
        }

        override fun onAdFailedToLoad(adError: ApAdError?) {
            super.onAdFailedToLoad(adError)
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();

        }

        override fun onAdFailedToShow(adError: ApAdError?) {
            super.onAdFailedToShow(adError)
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();

        }

        override fun onNextAction() {
            super.onNextAction()
            if (isDestroyed || isFinishing || isOnStop) return
            openNextScreen();

        }
    }

    override fun actionAfterFetchData() {
        startNotPurchase()
        initAdsNativeLanguage(this)
    }

    override fun setUpRemoteConfig() {
        val mRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else 3600)
            .build()

        mRemoteConfig.setConfigSettingsAsync(configSettings)
        mRemoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                Log.d("FirebaseRemote", "fetch Success: ")
                fetchDataRemote(mRemoteConfig){
                    actionAfterFetchData()
                }
            }else{
                actionAfterFetchData()
                Log.e("FirebaseRemote", "fetchError")
            }
        }
    }

    override fun fetchDataRemote(firebaseRemoteConfig: FirebaseRemoteConfig, callback: () -> Unit) {
        AdsRemote.showAdsSplash = firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_SPLASH)
        AdsRemote.showBanner = firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_BANNER)
        AdsRemote.showNativeLanguage = firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_NATIVE_LANGUAGE)
        AdsRemote.showNativeOnboard = firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_NATIVE_ONBOARD)
        AdsRemote.showNativeHome = firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_NATIVE_HOME)
        AdsRemote.showNativeCall = firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_NATIVE_CALL)
        AdsRemote.showNativeSms = firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_NATIVE_SMS)
        AdsRemote.showInterHome = firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_INTER_HOME)
        AdsRemote.showAdsResume =  firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_RESUME)
        WrapAdsResume.instance.setUpAdsResume(AdsRemote.showAdsResume)
        Log.d("TAG", "getRemoteValue: fetdata = ${firebaseRemoteConfig.getBoolean(AdsRemote.REMOTE_ADS_SPLASH)}")

        callback()
        super.fetchDataRemote(firebaseRemoteConfig, callback)
    }


    private var adCallback = object : AperoAdCallback() {

        override fun onAdClosed() {
            super.onNextAction()
            openNextScreen();
        }
    }
    override fun checkShowSplashFail() {
        if (AdsRemote.showAdsSplash){
            AperoAd.getInstance().onCheckShowSplashWhenFail(this, adCallback, TIME_DELAY.toInt())
        }

    }
}