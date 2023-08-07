package com.mobiai.app.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.applovin.impl.sdk.c.b.fa
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.ultils.NotificationPermission
import com.mobiai.base.basecode.storage.SharedPreferenceUtils

abstract class BaseSplashActivity<V : ViewBinding> : BaseActivity<V>() {
    var isOnStop = false
    private var showNextScreenHandler = Handler(Looper.getMainLooper())
    private lateinit var notificationPermission: NotificationPermission
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private var screenLock: PowerManager.WakeLock? = null
    abstract fun getIdAdsSplash(): String

    abstract fun getIdNativeLanguage(): String

    abstract fun openNextScreen()

    companion object {
        private val  TAG  = BaseSplashActivity::class.java.name
        var isNativeLanguageLoading = false
        fun initAdsNativeLanguage(activity: AppCompatActivity) {
            if (isNativeLanguageLoading) return
            Log.d(
                TAG,
                "-------++++++> purchse : ${AppPurchase.getInstance().isPurchased} :first:  ${SharedPreferenceUtils.firstOpenApp} , value: ${App.getStorageCommon()?.nativeAdLanguage?.value}, ${AdsRemote.showNativeLanguage}"
            )
            if (!AppPurchase.getInstance().isPurchased
                && SharedPreferenceUtils.firstOpenApp
                && App.getStorageCommon()?.nativeAdLanguage?.value == null
                && AdsRemote.showNativeLanguage
            ) {
                isNativeLanguageLoading = true

                Log.d(
                    TAG,
                    "-------> ${AppPurchase.getInstance().isPurchased} : ${SharedPreferenceUtils.firstOpenApp} , value: ${App.getStorageCommon()?.nativeAdLanguage?.value}, ${AdsRemote.showNativeLanguage}"
                )
                AperoAd.getInstance().loadNativeAdResultCallback(
                    activity,
                    BuildConfig.native_language,
                    R.layout.layout_native_ads_language,
                    object : AperoAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            App.getStorageCommon()?.nativeAdLanguage?.postValue(nativeAd)
                            Log.d(TAG, "onNativeAdLoaded ---------------> $nativeAd")
                            isNativeLanguageLoading = false
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            App.getStorageCommon()?.nativeAdLanguage?.postValue(null)
                            Log.d(TAG, "onAdFailedToLoad ---------------> null")
                            isNativeLanguageLoading = false
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            Log.i(TAG, "onAdImpression: ---------------->")
                            isNativeLanguageLoading = false

                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            isNativeLanguageLoading = false
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            isNativeLanguageLoading = false

                        }

                        override fun onNextAction() {
                            super.onNextAction()
                            isNativeLanguageLoading = false
                        }
                    }
                )
            }else{
                App.getStorageCommon()?.nativeAdLanguage?.postValue(App.getStorageCommon()?.nativeAdLanguage?.value)
                isNativeLanguageLoading = false
            }
        }
    }

    private fun initPermissionLauncher() {
        notificationPermissionLauncher  =registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            setupRemoteConfig()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wakeUpScreen()
        notificationPermission = NotificationPermission(this)
        initPermissionLauncher()
        if (!notificationPermission.hasPostNotificationGranted()){
            notificationPermission.requestRuntimeNotificationPermission(notificationPermissionLauncher)
        }else{
            setupRemoteConfig()
        }

        Log.i(TAG, "onCreate: ")
    }


    @SuppressLint("InvalidWakeLockTag")
    protected fun wakeUpScreen() {
        screenLock = (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "mywakelocktag"
        )
        screenLock?.acquire(3 * 60 * 1000L)
    }


    override fun onStop() {
        super.onStop()
        isOnStop = true
        Log.i(TAG, "onStop: ")
    }

    override fun onStart() {
        super.onStart()
        isOnStop = false
        Log.i(TAG, "onStart: ")
    }

    override fun onRestart() {
        super.onRestart()
        isOnStop = false
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
        showNextScreenHandler.removeCallbacksAndMessages(null)
    }

    fun setUpPurchase() {
        if (AppPurchase.getInstance().initBillingFinish) {
            showNextScreen()
        } else {
            AppPurchase.getInstance().setBillingListener({ i: Int ->
                runOnUiThread {
                    showNextScreen()
                }
            }, 2000)
        }
    }

    private fun showNextScreen() {
        if (AppPurchase.getInstance().isPurchased(this)) {
            showNextScreenHandler.postDelayed({
                openNextScreen()
            }, 2000)
        } else {
            startNotPurchase()
        }
    }


    open var isSkipCheckFailShowAds : Boolean = true

    override fun onResume() {
        super.onResume()
        if(AdsRemote.showAdsSplash && !isSkipCheckFailShowAds){
            checkShowSplashFail()
        }
        isSkipCheckFailShowAds = false
    }

    abstract fun startNotPurchase()
    abstract fun checkShowSplashFail()


}