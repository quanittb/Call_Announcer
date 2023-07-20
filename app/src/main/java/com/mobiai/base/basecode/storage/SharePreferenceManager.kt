package com.mobiai.base.basecode.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences


class SharedPreferencesManager(context: Context) {

    private val mPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {

        private const val PREF_NAME = "share_pre_my_app"

        private lateinit var instance: SharedPreferencesManager

        fun getInstance(): SharedPreferencesManager {
            return instance
        }
    }

    fun setValue(key: String?, value: String?) {
        mPref!!.edit()
            .putString(key, value)
            .apply()
    }

    fun setValue(key: String?, value: Float) {
        mPref!!.edit()
            .putFloat(key, value)
            .apply()
    }


    fun getValue(key: String?, defau: String?): String? {
        return mPref!!.getString(key, defau)
    }


    fun getValue(key: String?): String? {
        return mPref!!.getString(key, "")
    }


    fun setIntValue(key: String?, value: Int) {
        mPref!!.edit()
            .putInt(key, value)
            .apply()
    }

    fun setLongValue(key: String?, value: Long) {
        mPref!!.edit()
            .putLong(key, value)
            .apply()
    }

    fun getLongValue(key: String?, defaultValue: Long): Long {
        return mPref!!.getLong(key, defaultValue)
    }

    fun getIntValue(key: String?): Int {
        return mPref!!.getInt(key, 0)
    }

    fun getIntValue(key: String?, defaultValue: Int): Int {
        return mPref!!.getInt(key, defaultValue)
    }

    fun getValue(key: String?, defaultValues: Float): Float {
        return mPref!!.getFloat(key, defaultValues)
    }

    fun setValueBool(key: String?, value: Boolean) {
        mPref!!.edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getValueBool(key: String?): Boolean {
        return mPref!!.getBoolean(key, false)
    }

    fun getValueBool(key: String?, defaultValue: Boolean?): Boolean {
        return mPref!!.getBoolean(key, defaultValue!!)
    }

    fun remove(key: String?) {
        mPref!!.edit()
            .remove(key)
            .apply()
    }

    fun clear(): Boolean {
        return mPref!!.edit()
            .clear()
            .commit()
    }
}