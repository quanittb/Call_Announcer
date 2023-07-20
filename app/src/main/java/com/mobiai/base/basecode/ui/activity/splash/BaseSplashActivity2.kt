package com.mobiai.base.basecode.ui.activity.splash

import android.util.Log
import androidx.viewbinding.ViewBinding
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.mobiai.BuildConfig
import com.mobiai.app.ui.activity.SplashActivity
import com.mobiai.base.basecode.ads.TypeLoadAds

abstract class BaseSplashActivity2<V : ViewBinding> : BaseSplashActivity<V>() {

    companion object {
        private val TAG = SplashActivity::class.java.simpleName
    }

    abstract val typeShowAdsSplash: String

    abstract val idAdsHighFloor : String
    override fun startNotPurchase() {
        if (isShowAdsSplash) {
            when (typeShowAdsSplash) {
                TypeLoadAds.SAMETIME -> {
                    loadAdsSplashSameTime()
                }

                TypeLoadAds.ALTERNATE -> {
                    loadAdsSplashAlternate()
                }

                TypeLoadAds.OLD -> {
                    loadAdsSplashOld()
                }
            }
        } else {
            openNextScreen()
        }
    }

    private fun loadAdsSplashSameTime(){
        AperoAd.getInstance().loadSplashInterPrioritySameTime(
            this@BaseSplashActivity2,
            idAdsHighFloor,
            idAdsNormal,
            TIME_OUT,
            TIME_DELAY,
            false,
            aperoAdCallback
        )
    }
    private fun loadAdsSplashAlternate(){
        AperoAd.getInstance().loadSplashInterPriorityAlternate(
            this@BaseSplashActivity2,
            idAdsHighFloor,
            idAdsNormal,
            TIME_OUT,
            TIME_DELAY,
            true,
            aperoAdCallback
        )
    }
    private fun loadAdsSplashOld(){
        AperoAd.getInstance().setInitCallback {
            AperoAd.getInstance().loadSplashInterstitialAds(
                this@BaseSplashActivity2,
                idAdsNormal,
                TIME_OUT,
                TIME_DELAY,
                false,
                adCallback
            )
        }
    }

    var adCallback: AperoAdCallback = object : AperoAdCallback() {
        override fun onNextAction() {
            super.onNextAction()
            Log.d(TAG, "onNextAction: adCallback  $isFinishing || ${(isOnStop && isOpenActivityAfterShowAds)} || $isDestroyed")
            if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
            openNextScreen()
        }

        override fun onAdSplashReady() {
            super.onAdSplashReady()
            if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
            AperoAd.getInstance().onShowSplash(this@BaseSplashActivity2, object : AperoAdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    Log.d(TAG, "onNextAction: onAdSplashReady adCallback")
                    if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
                    openNextScreen()
                }

            })
        }
    }

    protected val aperoAdCallback = object : AperoAdCallback() {
        override fun onAdSplashReady() {
            Log.e(TAG, "aperoAdCallback - onAdSplashReady")
            super.onAdSplashReady()
            AperoAd.getInstance()
                .onShowSplashPriority(this@BaseSplashActivity2, object : AperoAdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
                        openNextScreen()
                    }
                })
        }

        override fun onNextAction() {
            Log.e(TAG, "aperoAdCallback - onNextAction $isFinishing || ${(isOnStop && isOpenActivityAfterShowAds)} || $isDestroyed")
            super.onNextAction()
            if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
            openNextScreen()
        }

        override fun onNormalInterSplashLoaded() {
            Log.e(TAG, "aperoAdCallback - onNormalInterSplashLoaded")
            super.onNormalInterSplashLoaded()
            AperoAd.getInstance().onShowSplash(this@BaseSplashActivity2, object : AperoAdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
                    openNextScreen()
                }
            })
        }
    }



}