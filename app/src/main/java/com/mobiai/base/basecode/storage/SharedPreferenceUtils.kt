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
    private const val VOLUME_RING = "VOLUME_RING"

    private const val SAVED_RINGTONE_ID ="SAVED_RINGTONE_ID"
    private const val SAVED_RINGTONE_PATH ="SAVED_RINGTONE_PATH"

    private const val BATTERY_MIN = "BATTERY_MIN"
    private const val BEFORE_MODE = "BEFORE_MODE"

    private const val SEEKBAR_RING = "SEEKBAR_RING"
    private const val SEEKBAR_MUSIC = "SEEKBAR_MUSIC"
    private const val CHECK_BEFORE = "CHECK_BEFORE"
    var firstOpenApp: Boolean
        get() = App.instanceSharePreference.getValueBool(FIRST_OPEN_APP, true)
        set(value) = App.instanceSharePreference.setValueBool(FIRST_OPEN_APP, value)

    var languageCode: String?
        get() = App.instanceSharePreference.getValue(LANGUAGE, null)
        set(value) = App.instanceSharePreference.setValue(LANGUAGE, value)

    var isCompleteOnboarding : Boolean
        get() =
            //if (BuildConfig.DEBUG) false else
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
        get() = App.instanceSharePreference.getIntValue(VOLUME_ANNOUNCER, 15)
        set(value) = App.instanceSharePreference.setIntValue(VOLUME_ANNOUNCER, value)
    var speedSpeak: Int
        get() = App.instanceSharePreference.getIntValue(SPEED_SPEAK, 50)
        set(value) = App.instanceSharePreference.setIntValue(SPEED_SPEAK, value)
    var isUnknownNumber: Boolean
        get() = App.instanceSharePreference.getValueBool(UNKNOWN_NUMBER, false)
        set(value) = App.instanceSharePreference.setValueBool(UNKNOWN_NUMBER, value)
    var isReadName: Boolean
        get() = App.instanceSharePreference.getValueBool(READ_NAME, false)
        set(value) = App.instanceSharePreference.setValueBool(READ_NAME, value)
    var volumeRing: Int
        get() = App.instanceSharePreference.getIntValue(VOLUME_RING, 15)
        set(value) = App.instanceSharePreference.setIntValue(VOLUME_RING, value)

    var currentMusic : Int
        get() = App.instanceSharePreference.getIntValue(CURRENT_MUSIC,100)
        set(value) = App.instanceSharePreference.setIntValue(CURRENT_MUSIC,value)
    var currentRing : Int
        get() = App.instanceSharePreference.getIntValue(CURRENT_RING,100)
        set(value) = App.instanceSharePreference.setIntValue(CURRENT_RING,value)

    var saved_ringtone_id:  Int
        get() = App.instanceSharePreference.getIntValue(SAVED_RINGTONE_ID, -1)
        set(value) = App.instanceSharePreference.setIntValue(SAVED_RINGTONE_ID, value)
    var saved_ringtone_path:  String?
        get() = App.instanceSharePreference.getValue(SAVED_RINGTONE_PATH, null)
        set(value) = App.instanceSharePreference.setValue(SAVED_RINGTONE_PATH, value)
    var batteryMin : Int
        get() = App.instanceSharePreference.getIntValue(BATTERY_MIN,20)
        set(value) = App.instanceSharePreference.setIntValue(BATTERY_MIN,value)
    var beforeMode : Int
        get() = App.instanceSharePreference.getIntValue(BEFORE_MODE,2)
        set(value) = App.instanceSharePreference.setIntValue(BEFORE_MODE,value)
    var seekBarRing: Int
        get() = App.instanceSharePreference.getIntValue(SEEKBAR_RING, 50)
        set(value) = App.instanceSharePreference.setIntValue(SEEKBAR_RING, value)
    var seekBarMusic: Int
        get() = App.instanceSharePreference.getIntValue(SEEKBAR_MUSIC, 50)
        set(value) = App.instanceSharePreference.setIntValue(SEEKBAR_MUSIC, value)
    var checkMode: Boolean
        get() = App.instanceSharePreference.getValueBool(CHECK_BEFORE, true)
        set(value) = App.instanceSharePreference.setValueBool(CHECK_BEFORE, value)
}