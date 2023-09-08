package com.mobiai.app.storage

import com.mobiai.BuildConfig
import com.mobiai.app.App

object AdsRemote {
    const val REMOTE_ADS_SPLASH = "inter_splash"
    const val REMOTE_ADS_RESUME = "ad_resume"

    const val REMOTE_ADS_NATIVE_LANGUAGE = "native_language"
    const val REMOTE_ADS_NATIVE_ONBOARD = "native_onboard"
    const val REMOTE_ADS_NATIVE_HOME = "native_home"
    const val REMOTE_ADS_NATIVE_CALL = "native_call"
    const val REMOTE_ADS_NATIVE_SMS = "native_sms"

    const val REMOTE_ADS_INTER_HOME = "inter_home"
    const val REMOTE_ADS_BANNER = "banner"
    const val REMOTE_SHOW_ADS_COLLAPSIBLE_BANNER = "collapsiblebanner_home"


    var showAdsSplash : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
            App.instanceSharePreference.getValueBool(REMOTE_ADS_SPLASH, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_SPLASH, value)

    var showAdsResume : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
                App.instanceSharePreference.getValueBool(REMOTE_ADS_RESUME, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_RESUME, value)

    var showNativeLanguage : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
                App.instanceSharePreference.getValueBool(REMOTE_ADS_NATIVE_LANGUAGE, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_NATIVE_LANGUAGE, value)


    var showNativeOnboard : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
            App.instanceSharePreference.getValueBool(REMOTE_ADS_NATIVE_ONBOARD, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_NATIVE_ONBOARD, value)


    var showNativeHome : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
            App.instanceSharePreference.getValueBool(REMOTE_ADS_NATIVE_HOME, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_NATIVE_HOME, value)


    var showNativeCall : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
            App.instanceSharePreference.getValueBool(REMOTE_ADS_NATIVE_CALL, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_NATIVE_CALL, value)


    var showNativeSms : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
                App.instanceSharePreference.getValueBool(REMOTE_ADS_NATIVE_SMS, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_NATIVE_SMS, value)

    var showInterHome : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
                App.instanceSharePreference.getValueBool(REMOTE_ADS_INTER_HOME, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_INTER_HOME, value)


    var showBanner : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
                App.instanceSharePreference.getValueBool(REMOTE_ADS_BANNER, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_BANNER, value)

    var showCollapsibleBanner : Boolean
        get() =
            if (BuildConfig.DEBUG) true else
                App.instanceSharePreference.getValueBool(REMOTE_SHOW_ADS_COLLAPSIBLE_BANNER, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_SHOW_ADS_COLLAPSIBLE_BANNER, value)


}