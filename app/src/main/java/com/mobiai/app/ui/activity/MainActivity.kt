package com.mobiai.app.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.billing.AppPurchase
import com.apero.inappupdate.AppUpdate
import com.apero.inappupdate.AppUpdateManager
import com.mobiai.BuildConfig
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.ui.fragment.HomeFragment
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.service.db.ModelTestDB
import com.mobiai.base.basecode.service.db.testModelDB
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.BaseActivity
import com.mobiai.databinding.ActivityMainBinding
import java.util.logging.Handler

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val TAG = MainActivity::javaClass.name
    companion object {
        fun startMain(context: Context, clearTask: Boolean) {
            val intent = Intent(context, MainActivity::class.java).apply {
                if (clearTask) {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        }

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun createView() {
        showFullscreen(true)
        AppUpdateManager.getInstance(this).isStartSessionFromOtherApp = false
        AppUpdateManager.getInstance(this).checkUpdateApp(this) {
            AppOpenManager.getInstance().disableAppResume()
        }
        //initAdsInterHome()
        attachFragment()
    }

    private fun attachFragment() {
        Log.i(TAG, "attachFragment: ACTmain aaaaaaaaaaa")
        addFragment(HomeFragment.instance())
    }

    override fun onResume() {
        super.onResume()
        SharedPreferenceUtils.languageCode?.let { LanguageUtil.changeLang(it, this) }
        setFullscreen()
        AppUpdateManager.getInstance(this).checkNewAppVersionState(this)

    }
    private fun initAdsInterHome() {
        Log.i(
            "TAG",
            "initAdsInterAddZodiac: isReady = ${App.getStorageCommon()?.mInterHome?.isReady}"
        )
        if (!AppPurchase.getInstance().isPurchased
            && AdsRemote.showInterHome
            && App.getStorageCommon().mInterHome == null
        ) {
            Log.i("TAG", "initAdsInterAddZodiac: compatibility fragment")
            AperoAd.getInstance().getInterstitialAds(
                this,
                BuildConfig.inter_home,
                object : AperoAdCallback() {
                    override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        App.getStorageCommon().mInterHome = interstitialAd
                    }
                }
            )
        }
    }

    override fun onNetworkAvailable() {
        runOnUiThread{
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                Log.i(TAG, "onNetworkAvailable: ACTmain bbbbbbbb")
                initAdsInterHome()
            }, 100)
        }
        super.onNetworkAvailable()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppUpdate.REQ_CODE_VERSION_UPDATE) {
            if (resultCode == Activity.RESULT_OK) {
                AppOpenManager.getInstance().disableAppResume()
            } else {
                if (AppUpdateManager.getInstance(this)
                        .getStyleUpdate() == AppUpdateManager.STYLE_FORCE_UPDATE
                ) {
                    AppOpenManager.getInstance().disableAppResume()
                } else {
                    AppOpenManager.getInstance().enableAppResume()
                }
            }
            AppUpdateManager.getInstance(this).onCheckResultUpdate(requestCode, resultCode) {
                AppOpenManager.getInstance().disableAppResume()
            }
        }
    }
}