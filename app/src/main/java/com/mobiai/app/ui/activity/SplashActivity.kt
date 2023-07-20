package com.mobiai.app.ui.activity

import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.apero.inappupdate.AppUpdateManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.storage.FirebaseRemote
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
            LanguageActivity.start(this, true)
        }  else {
            MainActivity.startMain(this, true)
        }
    }

    override fun startNotPurchase() {
        if(isShowAdsSplash){
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

        //TODO init ads native onboarding or native language

    }

    override fun setUpRemoteConfig() {

        //TODO setup remote

        actionAfterFetchData()
    }

    override fun fetchDataRemote(firebaseRemoteConfig: FirebaseRemoteConfig, callback: () -> Unit) {

        //Please setup data before use super fun
        //TODO please use fun fetchDataRemote() to get date from Firebase
        super.fetchDataRemote(firebaseRemoteConfig, callback)
    }


    override fun checkShowSplashFail() {
        AperoAd.getInstance().onCheckShowSplashWhenFail(this, adAperoCallBack, TIME_DELAY.toInt())
    }
}