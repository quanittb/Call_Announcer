package com.mobiai.app.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    companion object{
        fun instance() : HomeFragment{
            return newInstance(HomeFragment::class.java)
        }
    }
    override fun initView() {
        binding.cvItemHomeCall.setOnSafeClickListener(500) {
            addFragment(CallAnnouncerFragment.instance())
        }

        binding.cvItemHomeSMS.setOnSafeClickListener(500) {
            addFragment(SmsAnnouncerFragment.instance())
        }

        binding.cvItemHomeSetting.setOnSafeClickListener(500) {
            addFragment(SettingFragment.instance())
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container,false)
    }
}