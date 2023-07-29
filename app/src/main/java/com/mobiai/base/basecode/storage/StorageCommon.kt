package com.mobiai.base.basecode.storage

import androidx.lifecycle.MutableLiveData
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.ads.wrapper.ApNativeAd

class StorageCommon {
    var nativeAdLanguage = MutableLiveData<ApNativeAd>()
    var nativeAdOnboard = MutableLiveData<ApNativeAd>()
    var nativeAdHome = MutableLiveData<ApNativeAd>()
    var nativeAdCall = MutableLiveData<ApNativeAd>()
    var nativeAdSms = MutableLiveData<ApNativeAd>()

    var mInterHome : ApInterstitialAd? = null


}