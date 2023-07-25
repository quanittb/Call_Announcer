package com.mobiai.base.basecode.storage

import com.mobiai.BuildConfig
import com.mobiai.app.App

object SharedPreferenceUtils {
    private const val FIRST_OPEN_APP = "FIRST_OPEN_APP"
    private const val LANGUAGE = "LANGUAGE"
    private const val IS_COMPLETE_ONBOARDING = "IS_COMPLETE_ONBOARDING"

    private const val IS_TURN_ON_CALL_ANNOUNCER = "IS_TURN_ON_CALL_ANNOUNCER"
    private const val IS_TURN_ON_SMS_ANNOUNCER = "IS_TURN_ON_SMS_ANNOUNCER"

    private const val IS_TURN_ON_MODE_NORMAL_ANNOUNCER = "IS_TURN_ON_MODE_NORMAL_ANNOUNCER"
    private const val IS_TURN_ON_MODE_VIBRATE_ANNOUNCER = "IS_TURN_ON_MODE_VIBRATE_ANNOUNCER"
    private const val IS_TURN_ON_MODE_SILENT_ANNOUNCER = "IS_TURN_ON_MODE_SILENT_ANNOUNCER"
    private const val IS_TURN_ON_FLASH_ANNOUNCER = "IS_TURN_ON_FLASH_ANNOUNCER"
    private const val UNKNOWN_NUMBER = "UNKNOWN_NUMBER"
    private const val READ_NAME = "READ_NAME"

    private const val IS_TURN_ON_SMS_NORMAL_ANNOUNCER = "IS_TURN_ON_SMS_NORMAL_ANNOUNCER"
    private const val IS_TURN_ON_SMS_VIBRATE_ANNOUNCER = "IS_TURN_ON_SMS_VIBRATE_ANNOUNCER"
    private const val IS_TURN_ON_SMS_SILENT_ANNOUNCER = "IS_TURN_ON_SMS_SILENT_ANNOUNCER"
    private const val IS_TURN_ON_SMS_FLASH_ANNOUNCER = "IS_TURN_ON_SMS_FLASH_ANNOUNCER"
    private const val UNKNOWN_NUMBER_SMS = "UNKNOWN_NUMBER_SMS"
    private const val READ_NAME_SMS = "READ_NAME_SMS"

    private const val VOLUME_ANNOUNCER = "VOLUME_ANNOUNCER"
    private const val SPEED_SPEAK = "SPEED_SPEAK"
    private const val CURRENT_MUSIC = "CURRENT_MUSIC"
    private const val CURRENT_RING = "CURRENT_RING"

    var firstOpenApp: Boolean
        get() = App.instanceSharePreference.getValueBool(FIRST_OPEN_APP, true)
        set(value) = App.instanceSharePreference.setValueBool(FIRST_OPEN_APP, value)

    var languageCode: String?
        get() = App.instanceSharePreference.getValue(LANGUAGE, null)
        set(value) = App.instanceSharePreference.setValue(LANGUAGE, value)

    var isCompleteOnboarding : Boolean
        get() =
               App.instanceSharePreference.getValueBool(IS_COMPLETE_ONBOARDING, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_COMPLETE_ONBOARDING, value)

    var isTurnOnSmsNormal: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_SMS_NORMAL_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_SMS_NORMAL_ANNOUNCER, value)

    var isTurnOnSmsVibrate: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_SMS_VIBRATE_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_SMS_VIBRATE_ANNOUNCER, value)
    var isTurnOnSmsSilent: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_SMS_SILENT_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_SMS_SILENT_ANNOUNCER, value)
    var isTurnOnFlashSms: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_SMS_FLASH_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_SMS_FLASH_ANNOUNCER, value)

    var isUnknownNumberSms: Boolean
        get() = App.instanceSharePreference.getValueBool(UNKNOWN_NUMBER_SMS, false)
        set(value) = App.instanceSharePreference.setValueBool(UNKNOWN_NUMBER_SMS, value)
    var isReadNameSms: Boolean
        get() = App.instanceSharePreference.getValueBool(READ_NAME_SMS, false)
        set(value) = App.instanceSharePreference.setValueBool(READ_NAME_SMS, value)


    var isTurnOnCall: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_CALL_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_CALL_ANNOUNCER, value)

    var isTurnOnSms: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_SMS_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_SMS_ANNOUNCER, value)
    var isTurnOnModeNormal: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_MODE_NORMAL_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_MODE_NORMAL_ANNOUNCER, value)
    var isTurnOnModeVibrate: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_MODE_VIBRATE_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_MODE_VIBRATE_ANNOUNCER, value)
    var isTurnOnModeSilent: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_MODE_SILENT_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_MODE_SILENT_ANNOUNCER, value)
    var isTurnOnFlash: Boolean
        get() = App.instanceSharePreference.getValueBool(IS_TURN_ON_FLASH_ANNOUNCER, false)
        set(value) = App.instanceSharePreference.setValueBool(IS_TURN_ON_FLASH_ANNOUNCER, value)
    var volumeAnnouncer: Int
        get() = App.instanceSharePreference.getIntValue(VOLUME_ANNOUNCER, 30)
        set(value) = App.instanceSharePreference.setIntValue(VOLUME_ANNOUNCER, value)
    var speedSpeak: Int
        get() = App.instanceSharePreference.getIntValue(SPEED_SPEAK, 30)
        set(value) = App.instanceSharePreference.setIntValue(SPEED_SPEAK, value)
    var isUnknownNumber: Boolean
        get() = App.instanceSharePreference.getValueBool(UNKNOWN_NUMBER, false)
        set(value) = App.instanceSharePreference.setValueBool(UNKNOWN_NUMBER, value)
    var isReadName: Boolean
        get() = App.instanceSharePreference.getValueBool(READ_NAME, false)
        set(value) = App.instanceSharePreference.setValueBool(READ_NAME, value)






    var currentMusic : Int
        get() = App.instanceSharePreference.getIntValue(CURRENT_MUSIC,100)
        set(value) = App.instanceSharePreference.setIntValue(CURRENT_MUSIC,value)
    var currentRing : Int
        get() = App.instanceSharePreference.getIntValue(CURRENT_RING,100)
        set(value) = App.instanceSharePreference.setIntValue(CURRENT_RING,value)
}