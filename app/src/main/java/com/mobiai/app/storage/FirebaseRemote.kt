package com.mobiai.app.storage

import com.mobiai.app.App

object FirebaseRemote {
    const val KEY_UPDATE_OFF = "force_update"
    const val SHOW_UPDATE_TIMES = "show_update_times"
    const val REMOTE_KEY_UPDATE = "update_state"

    var keyUpdateState: String?
        get() = App.instanceSharePreference.getValue(REMOTE_KEY_UPDATE, KEY_UPDATE_OFF)
        set(value) = App.instanceSharePreference.setValue(REMOTE_KEY_UPDATE, value)

    var showUpdateTimes: Int
        get() = App.instanceSharePreference.getIntValue(SHOW_UPDATE_TIMES, 1)
        set(value) = App.instanceSharePreference.setIntValue(SHOW_UPDATE_TIMES, value)
}