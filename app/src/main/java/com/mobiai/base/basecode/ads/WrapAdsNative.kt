package com.mobiai.base.basecode.ads

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.storage.AdsRemote
import com.mobiai.base.basecode.ads.TypeLoadAds.Companion.ALTERNATE
import com.mobiai.base.basecode.ads.TypeLoadAds.Companion.OLD
import com.mobiai.base.basecode.ads.TypeLoadAds.Companion.SAMETIME
import com.mobiai.base.basecode.storage.SharedPreferenceUtils

class WrapAdsNative(
    val activity: Activity,
    val onLoadNativeListener: OnLoadNativeListener?,
    var idNormal: String,
    var layoutAds: Int
) {

    private val TAG = WrapAdsNative::class.java.name
    private var apNativeAdOld: ApNativeAd? = null
    private var isFinishLoadAdsHighFloor = false
    private var isFinishLoadAdsOld = false

    var isPostValue = false
    var valueAds: MutableLiveData<ApNativeAd>? = MutableLiveData<ApNativeAd>()
    var idHighFloor: String = ""
    var remoteLoadAds: String = OLD

    private fun initAdsNativeHighFloor() {
        if (!AppPurchase.getInstance().isPurchased) {
            when (remoteLoadAds) {
                SAMETIME -> {
                    initAdsNativeSameTime()
                }

                ALTERNATE -> {
                    initAdsNativeAlternate()
                }

                else -> {
                    initAdsNativeOld()
                }
            }
        }
    }

    fun initAdsNativeOld() {
        if (!AppPurchase.getInstance().isPurchased) {
            AperoAd.getInstance().loadNativeAdResultCallback(
                activity,
                idNormal,
                layoutAds,
                object : AperoAdCallback() {
                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        onLoadNativeListener?.onLoadAdsFail()
                        if (isPostValue) {
                            valueAds?.postValue(null)
                        }
                    }

                    override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        onLoadNativeListener?.onLoadAdsFinish(nativeAd)
                        if (isPostValue) {
                            valueAds?.postValue(nativeAd)
                        }
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        onLoadNativeListener?.onAdsImpression()
                    }
                })
        }
    }


    private fun initAdsNativeSameTime() {
        isFinishLoadAdsHighFloor = false
        isFinishLoadAdsOld = false

        AperoAd.getInstance().loadNativeAdResultCallback(
            activity,
            idHighFloor,
            layoutAds,
            object : AperoAdCallback() {
                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                    if (isFinishLoadAdsOld) {
                        Log.d(TAG, "onAdFailedToLoad: Same time")
                        onLoadNativeListener?.onLoadAdsFinish(apNativeAdOld)
                        if (isPostValue) {
                            valueAds?.postValue(apNativeAdOld)
                        }
                    } else {
                        isFinishLoadAdsHighFloor = true;
                    }
                }

                override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                    super.onNativeAdLoaded(nativeAd)
                    Log.d(TAG, "onNativeAdLoaded: Same time $isPostValue")
                    onLoadNativeListener?.onLoadAdsFinish(nativeAd)
                    if (isPostValue) {
                        valueAds?.postValue(nativeAd)
                    }
                }

                override fun onAdFailedToShow(adError: ApAdError?) {
                    super.onAdFailedToShow(adError)
                    Log.d(TAG, "onAdFailedToShow: Sametime")
                }

                override fun onNextAction() {
                    super.onNextAction()

                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    onLoadNativeListener?.onAdsImpression()
                }
            })

//        if (valueAds.value == null) {  //TODO check value before init ads
        AperoAd.getInstance().loadNativeAdResultCallback(
            activity,
            idHighFloor,
            layoutAds,
            object : AperoAdCallback() {
                override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                    super.onNativeAdLoaded(nativeAd)
                    if (isFinishLoadAdsHighFloor) {
                        Log.d(TAG, "onNativeAdLoaded: same time 2")
                        onLoadNativeListener?.onLoadAdsFinish(nativeAd)
                        if (isPostValue) {
                            valueAds?.postValue(nativeAd)
                        }
                    } else {
                        isFinishLoadAdsOld = true
                        apNativeAdOld = nativeAd
                    }
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                    if (isFinishLoadAdsHighFloor) {
                        onLoadNativeListener?.onLoadAdsFail()
                        if (isPostValue) {
                            valueAds?.postValue(null)
                        }
                    } else {
                        isFinishLoadAdsOld = true
                        apNativeAdOld = null
                    }
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    onLoadNativeListener?.onAdsImpression()
                }
            }
        )

    }

    private fun initAdsNativeAlternate() {
        AperoAd.getInstance().loadNativeAdResultCallback(
            activity,
            idHighFloor,
            layoutAds,
            object : AperoAdCallback() {
                override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                    super.onNativeAdLoaded(nativeAd)
                    onLoadNativeListener?.onLoadAdsFinish(nativeAd)
                    if (isPostValue) {
                        valueAds?.postValue(nativeAd)
                    }
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                    initAdsNativeOld()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    onLoadNativeListener?.onAdsImpression()
                }
            })
    }
}


interface OnLoadNativeListener {
    fun onLoadAdsFinish(nativeAd: ApNativeAd?)

    fun onLoadAdsFail()

    fun onAdsImpression()

}

