package com.mobiai.base.basecode.ui.activity.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.viewbinding.ViewBinding
import com.ads.control.admob.Admob
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.apero.inappupdate.AppUpdateManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.storage.FirebaseRemote
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.BaseActivity

abstract class BaseSplashActivity<V : ViewBinding> : BaseActivity<V>() {
    companion object {
        val TIME_OUT = 20000L
        val TIME_DELAY = 2000L
        const val WAKE_LOCK_SPLASH = "wake_lock_splash"
    }

    var isOnStop = false

    open var isOpenActivityAfterShowAds : Boolean = true

    open var isSkipCheckFailShowAds : Boolean = true

    abstract val isShowAdsSplash : Boolean

    private var showNextScreenHandler = Handler(Looper.getMainLooper())

    private var screenLock: PowerManager.WakeLock? = null

    abstract val idAdsNormal : String

    abstract fun openNextScreen()

    abstract fun startNotPurchase()

    abstract fun actionAfterFetchData()
    abstract fun setUpRemoteConfig()

    abstract fun checkShowSplashFail()

    open fun setUpAds(){
        Admob.getInstance().setOpenActivityAfterShowInterAds(isOpenActivityAfterShowAds)
    }
    open fun fetchDataRemote(firebaseRemoteConfig: FirebaseRemoteConfig, callback : () -> Unit){
        actionAfterFetchData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wakeUpScreen()
        setUpAds()
        setUpRemoteConfig()
        configUpdate()
    }

    @SuppressLint("InvalidWakeLockTag")
    protected fun wakeUpScreen() {
        screenLock = (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            WAKE_LOCK_SPLASH
        )
        screenLock?.acquire(3 * 60 * 1000L)
    }

    override fun onStop() {
        super.onStop()
        isOnStop = true
    }

    override fun onStart() {
        super.onStart()
        isOnStop = false
    }

    override fun onDestroy() {
        super.onDestroy()
        showNextScreenHandler.removeCallbacksAndMessages(null)
    }

    private fun setUpPurchase() {
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

    override fun onResume() {
        super.onResume()
        if(isShowAdsSplash && !isSkipCheckFailShowAds){
            checkShowSplashFail()
        }
        isSkipCheckFailShowAds = false
    }

    private fun configUpdate(){
        if(intent.data == null){
            FirebaseRemote.keyUpdateState?.let { AppUpdateManager.getInstance(this).setupUpdate(it, FirebaseRemote.showUpdateTimes) }
        }else{
            AppUpdateManager.getInstance(this).isStartSessionFromOtherApp = true
        }
    }


}