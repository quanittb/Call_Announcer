package com.mobiai.base.basecode.storage

import com.mobiai.BuildConfig
import com.mobiai.app.App

object SharedPreferenceUtils {
    private const val FIRST_OPEN_APP = "FIRST_OPEN_APP"
    private const val LANGUAGE = "LANGUAGE"
    private const val IS_COMPLETE_ONBOARDING = "IS_COMPLETE_ONBOARDING"

    var firstOpenApp: Boolean
        get() = App.instanceSharePreference.getValueBool(FIRST_OPEN_APP, true)
        set(value) = App.instanceSharePreference.setValueBool(FIRST_OPEN_APP, value)

    var languageCode: String?
        get() = App.instanceSharePreference.getValue(LANGUAGE, null)
        set(value) = App.instanceSharePreference.setValue(LANGUAGE, value)

    var isCompleteOnboarding : Boolean
        get() =
            if(BuildConfig.DEBUG) false
            else
                App.instanceSharePreference.getValueBool(IS_COMPLETE_ONBOARDING, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_COMPLETE_ONBOARDING, value)

}