package com.mobiai.base.basecode.ui.activity.splash

import android.util.Log
import androidx.viewbinding.ViewBinding
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.mobiai.base.basecode.ads.TypeLoadAds

abstract class BaseSplashActivity3<V : ViewBinding> : BaseSplashActivity<V>() {

    companion object {
        private val TAG = BaseSplashActivity3::class.java.name
    }

    abstract val typeLoadAdsSplash: String

    abstract val idAdsHighFloor: String

    abstract val idAdsMedium: String

    override fun startNotPurchase() {
        if (isShowAdsSplash) {
            when (typeLoadAdsSplash) {
                TypeLoadAds.SAMETIME -> {
                    loadAdsMediumSameTime()
                }

                TypeLoadAds.ALTERNATE -> {
                    loadAdsSplashMediumAlternate()
                }

                TypeLoadAds.OLD -> {
                    loadAdsSplashOld()
                }
            }
        } else {
            openNextScreen()
        }
    }

    private fun loadAdsMediumSameTime() {
        AperoAd.getInstance().loadSplashInterPriority3SameTime(
            this,
            idAdsHighFloor,
            idAdsMedium,
            idAdsNormal,
            TIME_OUT,
            TIME_DELAY,
            false,
            adAperoMediumCallback
        )
    }

    private fun loadAdsSplashMediumAlternate() {
        AperoAd.getInstance().loadSplashInterPriority3Alternate(
            this,
            idAdsHighFloor,
            idAdsMedium,
            idAdsNormal,
            TIME_OUT,
            TIME_DELAY,
            adAperoMediumCallback
        )

    }

    val adAperoMediumCallback: AperoAdCallback = object : AperoAdCallback() {
        override fun onAdFailedToLoad(adError: ApAdError?) {
            super.onAdFailedToLoad(adError)
            Log.d("TAG", "onAdFailedToLoad: ")
        }

        override fun onAdFailedToShow(adError: ApAdError?) {
            super.onAdFailedToShow(adError)
            Log.d("TAG", "onAdFailedToShow: ")
        }

        override fun onAdSplashReady() {
            super.onAdSplashReady()
            Log.e("TAG", "onAdSplashReady: ")
            AperoAd.getInstance()
                .onShowSplashPriority3(this@BaseSplashActivity3, object : AperoAdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
                        openNextScreen()
                    }
                })
        }

        override fun onNextAction() {
            super.onNextAction()
            if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
            Log.d("TAG", "adAperoCallBack -> onNextAction: ")
            openNextScreen()
        }

        override fun onNormalInterSplashLoaded() {
            super.onNormalInterSplashLoaded()
            Log.e("TAG", "onNormalInterSplashLoaded: ")
            AperoAd.getInstance().onShowSplash(this@BaseSplashActivity3, object : AperoAdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
                    openNextScreen()
                }
            })
        }

        override fun onAdSplashPriorityReady() {
            super.onAdSplashPriorityReady()
            AperoAd.getInstance()
                .onShowSplashPriority3(this@BaseSplashActivity3, object : AperoAdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
                        openNextScreen()
                    }
                })
        }

        override fun onAdSplashPriorityMediumReady() {
            super.onAdSplashPriorityMediumReady()
            Log.e("TAG", "onAdSplashPriorityMediumReady: ")
            AperoAd.getInstance()
                .onShowSplashPriority3(this@BaseSplashActivity3, object : AperoAdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
                        openNextScreen()
                    }
                })
        }
    }
    private fun loadAdsSplashOld() {
        AperoAd.getInstance().setInitCallback {
            AperoAd.getInstance().loadSplashInterstitialAds(
                this@BaseSplashActivity3,
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
            Log.d(TAG,
                "onNextAction: adCallback  $isFinishing || ${(isOnStop && isOpenActivityAfterShowAds)} || $isDestroyed"
            )
            if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
            openNextScreen()
        }

        override fun onAdSplashReady() {
            super.onAdSplashReady()
            if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
            AperoAd.getInstance()
                .onShowSplash(this@BaseSplashActivity3, object : AperoAdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        Log.d(TAG, "onNextAction: onAdSplashReady adCallback")
                        if (isFinishing || (isOnStop && isOpenActivityAfterShowAds) || isDestroyed) return
                        openNextScreen()
                    }

                })
        }
    }

}