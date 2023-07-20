package com.mobiai.app.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import com.ads.control.ads.AperoAd
import com.ads.control.billing.AppPurchase
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.adapter.LanguageAdapter
import com.mobiai.base.basecode.language.Language
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.BaseActivity
import com.mobiai.databinding.ActivityLanguageBinding

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    companion object {
        const val OPEN_FROM_MAIN = "open_from_main"
        fun start(context: Context, clearTask : Boolean = true){
            val intent = Intent(context, LanguageActivity::class.java).apply {
                if(clearTask){
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        }
    }

    var listLanguages: ArrayList<Language> = arrayListOf()
    var languageCode = "en"
    lateinit var languageAdapter: LanguageAdapter

    override fun getLayoutResourceId(): Int = R.layout.activity_language

    override fun getViewBinding(): ActivityLanguageBinding  = ActivityLanguageBinding.inflate(layoutInflater)

    override fun createView() {
        getDataLanguage()
        if (!intent.getBooleanExtra(OPEN_FROM_MAIN, false)) {
            initAds()
        } else {
            binding.frAds.visibility = View.GONE
            binding.imgBack.visibility = View.VISIBLE
        }
        binding.imgConfirm.setOnClickListener {
            changeLanguage()
            Log.d("TAGGG", "---------->  getLangueCode: $languageCode")
        }
        binding.imgBack.setOnClickListener {
            finish()
        }

    }

    private fun initAds() {
        if (AppPurchase.getInstance().isPurchased) {
            binding.frAds.visibility = View.GONE
        } else {
            App.getStorageCommon()?.nativeAdLanguage?.observe(this) {
                if (it != null) {
                    AperoAd.getInstance().populateNativeAdView(
                        this,
                        it,
                        binding.frAds,
                        binding.includeNative.shimmerContainerNative
                    )
                } else {
                    binding.frAds.visibility = View.GONE
                }
            }
        }
    }


    private fun initAdapter(){
        languageAdapter =  LanguageAdapter(this, object : LanguageAdapter.OnLanguageClickListener{
            override fun onClickItemListener(language: Language?) {
                languageCode = language!!.locale
            }
        })
        languageAdapter.setItems(listLanguages)
        binding.recyclerViewLanguage.adapter = languageAdapter
    }

    private fun getDataLanguage() {
        initData()
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Resources.getSystem().configuration.locale
        }
        var languageSystem: Language? = null
        var position = 0
        for (language in listLanguages) {
            if (language.locale.equals(locale.language)) {
                languageSystem = language
                languageCode = locale.language
            }
            if (intent.getBooleanExtra(OPEN_FROM_MAIN, false)) {
                if (SharedPreferenceUtils.languageCode == language.locale) {
                    languageSystem = language
                    languageCode = languageSystem.locale
                }
                position = listLanguages.indexOf(languageSystem)
            }
        }
        listLanguages[position].isChoose = true
        initAdapter()
    }

    private fun changeLanguage() {
        SharedPreferenceUtils.languageCode = languageCode

        LanguageUtil.changeLang(SharedPreferenceUtils.languageCode!!, this)
        SharedPreferenceUtils.firstOpenApp = false
        //TODO start Main
        MainActivity.startMain(this, true)
    }

    fun initData() {
        listLanguages = ArrayList()
        listLanguages.add(Language(R.drawable.flag_en, getString(R.string.language_english), "en"))
        listLanguages.add(Language(R.drawable.flag_es_spain,
            getString(R.string.language_spain),
            "es"))
        listLanguages.add(Language(R.drawable.flag_pt_portugal,
            getString(R.string.language_portugal),
            "pt"))
        listLanguages.add(Language(R.drawable.flag_in_hindi,
            getString(R.string.language_hindi),
            "in"))
        listLanguages.add(Language(R.drawable.flag_de_germany,
            getString(R.string.language_germany),
            "de"))
        listLanguages.add(Language(R.drawable.flag_fr_france,
            getString(R.string.language_france),
            "fr"))

    }

}