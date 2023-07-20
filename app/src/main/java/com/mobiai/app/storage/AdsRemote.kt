package com.mobiai.app.storage

import com.mobiai.app.App

object AdsRemote {
    const val REMOTE_ADS_SPLASH = "show_ads_splash"
    const val REMOTE_ADS_NATIVE_LANGUAGE = "show_native_language"


    var showAdsSplash : Boolean
        get() = App.instanceSharePreference.getValueBool(REMOTE_ADS_SPLASH, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_SPLASH, value)

    var showNativeLanguage : Boolean
        get() = App.instanceSharePreference.getValueBool(REMOTE_ADS_NATIVE_LANGUAGE, true)
        set(value) = App.instanceSharePreference.setValueBool(REMOTE_ADS_NATIVE_LANGUAGE, value)


}