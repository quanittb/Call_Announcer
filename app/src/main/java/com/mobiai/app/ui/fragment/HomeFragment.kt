package com.mobiai.app.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    companion object{
        fun instance() : HomeFragment{
            return newInstance(HomeFragment::class.java)
        }
    }
    override fun initView() {
        binding.cvItemHomeCall.setOnClickListener {
            addFragment(CallAnnouncerFragment.instance())
        }

        binding.cvItemHomeSMS.setOnClickListener {
            addFragment(SmsAnnouncerFragment.instance())
        }

        binding.cvItemHomeSetting.setOnClickListener {
            // todo setting
            addFragment(RingstoneFragment.instance())
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container,false)
    }
}