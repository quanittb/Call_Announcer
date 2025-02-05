package com.mobiai.base.basecode.ui.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.ultils.NetworkConnected
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ultility.RxBus
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


abstract class BaseActivity<V:ViewBinding > : AppCompatActivity(){

    companion object{
        private val  TAG  = BaseActivity::class.java.name
        var isLoadingNativeHome = false
        fun initAdsNativeHome(activity: AppCompatActivity) {
            if (isLoadingNativeHome) return

            if (!AppPurchase.getInstance().isPurchased
                && App.getStorageCommon()?.nativeAdHome?.value == null
                && AdsRemote.showNativeHome
            ) {
                isLoadingNativeHome = true
                AperoAd.getInstance().loadNativeAdResultCallback(
                    activity,
                    BuildConfig.native_home,
                    R.layout.layout_native_ads_home,
                    object : AperoAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            App.getStorageCommon()?.nativeAdHome?.postValue(nativeAd)
                            isLoadingNativeHome = false
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            App.getStorageCommon()?.nativeAdHome?.postValue(null)
                            isLoadingNativeHome = false

                        }
                        override fun onAdImpression() {
                            super.onAdImpression()
                            isLoadingNativeHome = false
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            isLoadingNativeHome = false
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            isLoadingNativeHome = false
                        }

                    }
                )
            }else{
                App.getStorageCommon()?.nativeAdHome?.postValue(App.getStorageCommon()?.nativeAdHome?.value)
                isLoadingNativeHome = false
            }
        }

        var isNativeLanguageLoading = false
        fun initAdsNativeLanguage(activity: AppCompatActivity) {
            if (isNativeLanguageLoading) return

            if (!AppPurchase.getInstance().isPurchased
                && SharedPreferenceUtils.firstOpenApp
                && App.getStorageCommon()?.nativeAdLanguage?.value == null
                && AdsRemote.showNativeLanguage
            ) {
                isNativeLanguageLoading = true

                AperoAd.getInstance().loadNativeAdResultCallback(
                    activity,
                    BuildConfig.native_language,
                    R.layout.layout_native_ads_language,
                    object : AperoAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            App.getStorageCommon()?.nativeAdLanguage?.postValue(nativeAd)
                            isNativeLanguageLoading = false
                            Log.i(TAG, "onNativeAdLoaded: $isNativeLanguageLoading", )

                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            App.getStorageCommon()?.nativeAdLanguage?.postValue(null)
                            isNativeLanguageLoading = false
                            Log.i(TAG, "onAdFailedToLoad: $isNativeLanguageLoading")
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            isNativeLanguageLoading = false
                            Log.i(TAG, "onAdImpression: $isNativeLanguageLoading")

                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            isNativeLanguageLoading = false
                            Log.i(TAG, "onAdFailedToShow: $isNativeLanguageLoading")
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            isNativeLanguageLoading = false
                            Log.i(TAG, "onAdLoaded: $isNativeLanguageLoading")

                        }

                        override fun onNextAction() {
                            super.onNextAction()
                            isNativeLanguageLoading = false
                            Log.i(TAG, "onNextAction: $isNativeLanguageLoading")
                        }
                    }
                )
            }else{
                App.getStorageCommon()?.nativeAdLanguage?.postValue(App.getStorageCommon()?.nativeAdLanguage?.value)
                isNativeLanguageLoading = false
                Log.i(TAG, "initAdsNativeLanguage: $isNativeLanguageLoading")
            }
        }
    }
    protected lateinit var binding : V
    private var onFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        decorView = window.decorView
        setFullscreen()
        createView()
        LanguageUtil.setupLanguage(this)
    }

    protected abstract fun getLayoutResourceId(): Int

    protected abstract fun getViewBinding() : V

    protected abstract fun createView()


    private var decorView: View? = null
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && onFullscreen) {
            decorView!!.systemUiVisibility = hideSystemBars()
        }
    }


    open fun hideSystemBars(): Int {
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    protected open fun setFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val windowInsetsController = window.insetsController
            if (windowInsetsController != null) {
                windowInsetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                windowInsetsController.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = hideSystemBars()
        }
    }

    open fun showKeyboard(view: View?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    open fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }

    open fun handleBackpress() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent);
        }

        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    open fun addFragment(fragment: Fragment, viewId: Int = android.R.id.content, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        transaction.add(viewId, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }


    open fun replaceFragment(fragment: Fragment, viewId: Int = android.R.id.content, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(viewId, fragment)
        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commit()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            onNetworkAvailable()
        }

        override fun onLost(network: Network) {
            onNetworkLost()
        }
    }

    protected open fun onNetworkAvailable() {
        Log.d(TAG, "-> onNetworkAvailable ")
        RxBus.publish(NetworkConnected(true))

    }

    protected open fun onNetworkLost() {
        Log.d(TAG, "-> onNetworkLost ")
        RxBus.publish(NetworkConnected(false))

    }

    override fun onResume() {
        super.onResume()
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun isOptimizeBattery() : Boolean{
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager?
        if (powerManager != null) {
            return if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
                Log.d(TAG, "-> has NOT ignoringBatteryBattery")
                false
            } else {
                Log.d(TAG, "-> has  ignoringBatteryBattery")
                true
            }
        }
        return true
    }

    protected fun requestBatteryOptimize(){
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun<T> runBackground(action: () -> T, onSuccess: (result: T) -> Unit = {}, onError: (t: Throwable) -> Unit = {}) : Disposable {
        val disposable =  Single.create {
            it.onSuccess(action()!!)
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                onSuccess(it)
            }, {
                onError(it)
            })
        compositeDisposable.add(
            disposable
        )
        return disposable
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()

    }

    protected fun showFullscreen(on: Boolean) {
        onFullscreen = on
        if (on)
            setFullscreen()
    }



}

