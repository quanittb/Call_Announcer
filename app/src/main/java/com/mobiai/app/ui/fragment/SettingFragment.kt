package com.mobiai.app.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jaygoo.widget.R
import com.mobiai.app.ui.activity.LanguageActivity
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentSettingAccountBinding

class SettingFragment: BaseFragment<FragmentSettingAccountBinding>() {
    companion object{
        fun instance() : SettingFragment{
            return newInstance(SettingFragment::class.java)
        }
    }

    override fun initView() {

        binding.sbSpeakingSpeed.setIndicatorTextDecimalFormat("0")
        binding.sbSpeakingSpeed.setIndicatorTextStringFormat("%s%%")
        binding.sbSpeakingSpeed.setProgress(0f, 100.0f)
        binding.sbVolume.setIndicatorTextDecimalFormat("0")
        binding.sbVolume.setIndicatorTextStringFormat("%s%%")
        binding.sbVolume.setProgress(0f, 100.0f)

        binding.lnRingtone.setOnClickListener {
            addFragment(RingstoneFragment.instance())
        }

        binding.lnLanguage.setOnClickListener {
            LanguageActivity.start(requireContext(),true)
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingAccountBinding = FragmentSettingAccountBinding.inflate(inflater,container,false)
}