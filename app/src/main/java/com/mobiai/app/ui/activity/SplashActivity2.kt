package com.mobiai.app.ui.activity

import com.ads.control.ads.AperoAd
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.storage.AdsRemote
import com.mobiai.base.basecode.ads.TypeLoadAds
import com.mobiai.base.basecode.ads.TypeLoadAds.Companion.ALTERNATE
import com.mobiai.base.basecode.ads.TypeLoadAds.Companion.SAMETIME
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.splash.BaseSplashActivity2
import com.mobiai.databinding.ActivitySplashBinding

class SplashActivity2(
    override val isShowAdsSplash: Boolean = AdsRemote.showAdsSplash,
    override val idAdsNormal: String = BuildConfig.inter_splash,
    override val typeShowAdsSplash: String = TypeLoadAds.SAMETIME,
    override val idAdsHighFloor: String = BuildConfig.inter_splash_priority
) : BaseSplashActivity2<ActivitySplashBinding>() {
    override fun openNextScreen() {
        if (SharedPreferenceUtils.languageCode == null) {
            LanguageActivity.start(this, true)
        }  else {
            MainActivity.startMain(this, true)
        }
    }

    override fun actionAfterFetchData() {
        startNotPurchase()

        //TODO init ads native onboarding or native language

    }

    override fun setUpRemoteConfig() {
        //TODO setup remote

        //TODO please use fun fetchDataRemote() to get date from Firebase
        actionAfterFetchData()

    }

    override fun fetchDataRemote(firebaseRemoteConfig: FirebaseRemoteConfig, callback: () -> Unit) {

        //Please setup data before use super fun

        super.fetchDataRemote(firebaseRemoteConfig, callback)
    }


    override fun checkShowSplashFail() {
        when(typeShowAdsSplash){
            SAMETIME, ALTERNATE -> {
                AperoAd.getInstance().onCheckShowSplashPriorityWhenFail(this@SplashActivity2, aperoAdCallback, TIME_DELAY.toInt())
            }
            else -> AperoAd.getInstance().onCheckShowSplashWhenFail(this@SplashActivity2, adCallback, TIME_DELAY.toInt() )
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_splash

    override fun getViewBinding(): ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)

    override fun createView() {
    }
}