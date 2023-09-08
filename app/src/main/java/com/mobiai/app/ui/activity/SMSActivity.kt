package com.mobiai.app.ui.activity

import android.content.Context
import android.content.Intent
import com.mobiai.R
import com.mobiai.app.ui.fragment.CallAnnouncerFragment
import com.mobiai.app.ui.fragment.SettingFragment
import com.mobiai.app.ui.fragment.SmsAnnouncerFragment
import com.mobiai.app.ultils.NetworkConnected
import com.mobiai.base.basecode.ads.WrapAdsResume
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.BaseActivity
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.base.basecode.ultility.RxBus
import com.mobiai.databinding.ActivityMainBinding


class SMSActivity : BaseActivity<ActivityMainBinding>() {
    companion object{
        private val TAG = SMSActivity::class.java.name
        fun start(context: Context ){
            val intent = Intent(context, SMSActivity::class.java).apply {
            }
            context.startActivity(intent)
        }

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun createView() {
        SharedPreferenceUtils.languageCode?.let { LanguageUtil.changeLang(it, this) }
        showFullscreen(true)
        attachFragment()
    }

    private fun attachFragment(){
        if (supportFragmentManager.backStackEntryCount > 0){
            return
        }
        addFragment(SmsAnnouncerFragment.instance())
    }

    override fun onStop() {
        super.onStop()
        if(BaseFragment.isGoToSetting){
            WrapAdsResume.instance.disableAdsResume()
        }
    }
}