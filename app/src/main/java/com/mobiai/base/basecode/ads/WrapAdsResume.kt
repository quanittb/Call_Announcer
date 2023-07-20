package com.mobiai.base.basecode.ads

import android.util.Log
import com.ads.control.admob.AppOpenManager

class WrapAdsResume {

    companion object {
        private val TAG = WrapAdsResume::class.java.name
        val instance: WrapAdsResume by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            WrapAdsResume()
        }
    }

    private var isShowAdsResume = false

    fun setUpAdsResume(isShow : Boolean){
        isShowAdsResume = isShow
        if(!isShow){
            AppOpenManager.getInstance().disableAppResume()
        }else{
            AppOpenManager.getInstance().enableAppResume()
        }
    }

    fun enableAdsResume(){
        if(isShowAdsResume){
            AppOpenManager.getInstance().enableAppResume()
        }else{
            AppOpenManager.getInstance().disableAppResume()
        }
    }

    fun disableAdsResume(){
        AppOpenManager.getInstance().disableAppResume()
    }

    fun disableAdsResumeByClickAction(){
        Log.d(TAG, "disableAdsResumeByClickAction: $isShowAdsResume")
        if(isShowAdsResume){
            AppOpenManager.getInstance().disableAdResumeByClickAction()
        }
    }

    fun enableAppResumeWithActivity(activity : Class<*>){
        if(!isShowAdsResume){
            AppOpenManager.getInstance().enableAppResumeWithActivity(activity::class.java)
        }
    }

}