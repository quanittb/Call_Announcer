package com.mobiai.app.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentSettingRingstoneBinding

class RingstoneFragment : BaseFragment<FragmentSettingRingstoneBinding>() {
    companion object{
        fun instance():RingstoneFragment {
            return newInstance(RingstoneFragment::class.java)
        }
    }
    override fun initView() {
        binding.sbRingtoneVolume?.setProgress(0f, 100f)
        binding.sbRingtoneVolume?.setIndicatorTextDecimalFormat("0")
        binding.sbRingtoneVolume?.setIndicatorTextStringFormat("%s%%")

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingRingstoneBinding = FragmentSettingRingstoneBinding.inflate(inflater,container,false)
}