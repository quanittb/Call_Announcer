package com.mobiai.app.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.ads.control.ads.AperoAd
import com.ads.control.billing.AppPurchase
import com.mobiai.R
import com.mobiai.app.App
import com.mobiai.app.adapter.LanguageAdapter
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.ultils.LanguageProvider
import com.mobiai.base.basecode.extensions.gone
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.language.Language
import com.mobiai.base.basecode.language.LanguageUtil
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.BaseActivity
import com.mobiai.databinding.ActivityLanguageBinding

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    companion object {
        const val OPEN_FROM_MAIN = "open_from_main"
        fun start(context: Context,openFromMain : Boolean = false, clearTask : Boolean = true){
            val intent = Intent(context, LanguageActivity::class.java).apply {
                if(clearTask){
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (openFromMain){
                    putExtra(OPEN_FROM_MAIN, true)
                }
            }
            context.startActivity(intent)
        }
    }

    var listLanguages: ArrayList<Language> = arrayListOf()
    var languageCode = "en"
    lateinit var languageAdapter: LanguageAdapter
    private lateinit var languageProvider: LanguageProvider

    override fun getLayoutResourceId(): Int = R.layout.activity_language

    override fun getViewBinding(): ActivityLanguageBinding  = ActivityLanguageBinding.inflate(layoutInflater)

    override fun createView() {
        SharedPreferenceUtils.languageCode?.let { LanguageUtil.changeLang(it, this) }
        Log.d("TAG", "createView: $languageCode")
        languageProvider = LanguageProvider(this)
        showFullscreen(true)
        getDataLanguage2()
        if (!intent.getBooleanExtra(OPEN_FROM_MAIN, false)) {
            showAdsNativeLanguage()
            binding.imgBack.visibility = View.INVISIBLE
            binding.tvTitle.text = resources.getString(R.string.languages_first_open)


        } else {
            binding.tvTitle.text = resources.getString(R.string.language)
            binding.frAds.gone()
            binding.imgBack.visible()
        }
        binding.imgConfirm.setOnClickListener {
            changeLanguage()
        }
        binding.imgBack.setOnClickListener {
            finish()
        }

    }

    private fun showAdsNativeLanguage() {
        if (AppPurchase.getInstance().isPurchased || !AdsRemote.showNativeLanguage) {
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
    private fun initAdapter2(listLanguage: MutableList<Language>) {
        languageCode = listLanguage[0].locale
        languageAdapter = LanguageAdapter(this, object : LanguageAdapter.OnLanguageClickListener {
            override fun onClickItemListener(language: Language?) {
                languageCode = language!!.locale
            }
        })
        languageAdapter.setItems(listLanguage)
        binding.recyclerViewLanguage.adapter = languageAdapter
    }
    private fun getDataLanguage2() {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Resources.getSystem().configuration.locale
        }
        val deviceLanguage = locale.language
        var selectedLanguage: Language? = null

        fun isLanguageInListFirstOpen(): Language? {
            for (language in languageProvider.listFirstOpenLanguage) {
                if (language.locale == deviceLanguage) {
                    Log.d("TAG", "isLanguageInListFirstOpen: $language")
                    return language
                }
            }
            return null
        }

        fun isLanguageInListSetting(): Language? {
            for (language in languageProvider.listSettingLanguage) {
                if (language.locale == deviceLanguage) {
                    return language
                }
            }
            return null
        }

        if (intent.getBooleanExtra(OPEN_FROM_MAIN, false)) {
            for (language in languageProvider.listSettingLanguage){
                if (SharedPreferenceUtils.languageCode == language.locale) {
                    selectedLanguage = language
                    languageCode = selectedLanguage.locale
                    break
                }
            }
            if (selectedLanguage != null) {
                languageProvider.listSettingLanguage.remove(selectedLanguage)
                languageProvider.listSettingLanguage.add(0, selectedLanguage)

            }
            languageProvider.listSettingLanguage[0].isChoose = true
            initAdapter2(languageProvider.listSettingLanguage)
        }

        else {
            selectedLanguage = isLanguageInListFirstOpen()
            if (selectedLanguage != null){
                languageCode = selectedLanguage.locale
                languageProvider.listFirstOpenLanguage.remove(selectedLanguage)
                languageProvider.listFirstOpenLanguage.add(0, selectedLanguage)

            }else{
                selectedLanguage = isLanguageInListSetting()
                if (selectedLanguage != null){
                    languageProvider.listFirstOpenLanguage.add(0, selectedLanguage)
                }
            }
            languageProvider.listFirstOpenLanguage[0].isChoose = true
            initAdapter2(languageProvider.listFirstOpenLanguage)
        }
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
            if (intent.getBooleanExtra(OPEN_FROM_MAIN, false)) {
                if (SharedPreferenceUtils.languageCode == language.locale) {
                    languageSystem = language
                    languageCode = languageSystem.locale
                }
            }else
            if (language.locale.equals(locale.language)) {
                languageSystem = language
                languageCode = locale.language
            }
        }
        if (languageSystem != null) {
            listLanguages.remove(languageSystem)
            listLanguages.add(0, languageSystem)
        }
        listLanguages[position].isChoose = true
        initAdapter()
    }

    private fun changeLanguage() {
        SharedPreferenceUtils.languageCode = languageCode
        LanguageUtil.changeLang(SharedPreferenceUtils.languageCode!!, this)
        SharedPreferenceUtils.firstOpenApp = false
        //TODO start Main
        if (intent.getBooleanExtra(OPEN_FROM_MAIN, false)) {
            MainActivity.startMain(this, true)
        } else if (!SharedPreferenceUtils.isCompleteOnboarding) {
            OnBoardingActivity.start(this, true)
        }
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

        listLanguages.add(Language(R.drawable.flag_de_germany,
            getString(R.string.language_germany),
            "de"))

        listLanguages.add(Language(R.drawable.flag_fr_france,
            getString(R.string.language_france),
            "fr"))

        listLanguages.add(Language(R.drawable.flag_vn_vietnam, getString(R.string.language_vietnamese), "vi"))

        if(intent.getBooleanExtra(OPEN_FROM_MAIN, false)){

            listLanguages.add(Language(R.drawable.flag_ko_korean,
                getString(R.string.language_korean),
                "ko"))

            listLanguages.add(Language(R.drawable.flag_hi_hindi,
                getString(R.string.language_hindi),
                "hi"))

            listLanguages.add(Language(R.drawable.flag_lr_liberia,
                getString(R.string.language_liberia),
                "lr"))

            listLanguages.add(Language(R.drawable.flag_it_italy,
                getString(R.string.language_italian),
                "it"))

            listLanguages.add(Language(R.drawable.flag_jp_japan,
                getString(R.string.language_japan),
                "ja"))

            listLanguages.add(Language(R.drawable.flag_gr_greece,
                getString(R.string.language_greece),
                "gr"))
        }
    }

    override fun onNetworkAvailable() {
        super.onNetworkAvailable()
        runOnUiThread {
            Handler(Looper.getMainLooper()).postDelayed({
                if (!intent.getBooleanExtra(OPEN_FROM_MAIN, false)) {
                        BaseSplashActivity.initAdsNativeLanguage(this)
                }
            }, 500)
        }
    }
}