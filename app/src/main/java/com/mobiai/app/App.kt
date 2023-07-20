package com.mobiai.app

import com.ads.control.admob.Admob
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.application.AdsMultiDexApplication
import com.ads.control.config.AdjustConfig
import com.ads.control.config.AperoAdConfig
import com.google.android.gms.ads.AdActivity
import com.mobiai.BuildConfig
import com.mobiai.app.ui.activity.SplashActivity
import com.mobiai.base.basecode.storage.SharedPreferencesManager
import com.mobiai.base.basecode.storage.StorageCommon

class App : AdsMultiDexApplication() {

    companion object {
        private var instance: App? = null
        private var storageCommon: StorageCommon? = null

        fun getStorageCommon(): StorageCommon? {
            return storageCommon
        }

        fun getInstance(): App? {
            return instance
        }

        lateinit var instanceSharePreference: SharedPreferencesManager
        val ADJUST_TOKEN: String = "ADJUST_TOKEN"
        val EVENT_AD_IMPRESSION_ADJUST: String = "EVENT_AD_IMPRESSION_ADJUST"
        val EVENT_PURCHASE_ADJUST: String = "EVENT_PURCHASE_ADJUST"

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        instanceSharePreference = SharedPreferencesManager(applicationContext)
        storageCommon = StorageCommon()
        initAds()

    }

    private  fun initAds() {
        val environment =
            if (BuildConfig.build_debug) AperoAdConfig.ENVIRONMENT_DEVELOP else AperoAdConfig.ENVIRONMENT_PRODUCTION
        aperoAdConfig = AperoAdConfig(this, AperoAdConfig.PROVIDER_ADMOB, environment)

        val adjustConfig = AdjustConfig( ADJUST_TOKEN)
        adjustConfig.eventAdImpression = EVENT_AD_IMPRESSION_ADJUST
        adjustConfig.eventNamePurchase = EVENT_PURCHASE_ADJUST
        aperoAdConfig.adjustConfig = adjustConfig

        //Ads resume

        aperoAdConfig.idAdResume = BuildConfig.openApp_resume

        aperoAdConfig.listDeviceTest = getListTestDeviceId()

        AperoAd.getInstance().init(this, aperoAdConfig, false);

        Admob.getInstance().setDisableAdResumeWhenClickAds(true)
        Admob.getInstance().setOpenActivityAfterShowInterAds(false);
        AppOpenManager.getInstance().disableAppResumeWithActivity(AdActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
    }

    private fun getListTestDeviceId(): MutableList<String> {
        val idTestList: MutableList<String> = ArrayList()
        idTestList.add("FF22226D0B3EE1C87C93FBE226C21ADC") // VSmart Joy 3
        idTestList.add("955BEB0FE62BECE2DC7B8F3C6D11EC0F") // pixel 4XL
        idTestList.add("E14D05CCCA816A8916E746D0AF22F995") // oppo A55
        idTestList.add("0A284B9036D54EC34DBF1957D10A7101") // real me c35
        idTestList.add("88846E2F8306D586DE6BD6ED0C01E55B") // Xiaomi Note 11S 2201117SG
        return idTestList
    }

}