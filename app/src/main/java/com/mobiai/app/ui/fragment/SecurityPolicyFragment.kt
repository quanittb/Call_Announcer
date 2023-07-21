package com.mobiai.app.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentSecurityPolicyBinding

class SecurityPolicyFragment : BaseFragment<FragmentSecurityPolicyBinding>() {
    companion object{
        fun instance() : SecurityPolicyFragment{
            return newInstance(SecurityPolicyFragment::class.java)
        }
    }

    override fun initView() {
        binding.icArrowLeftPolicy.setOnClickListener {
            handlerBackPressed()
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSecurityPolicyBinding = FragmentSecurityPolicyBinding.inflate(inflater,container,false)

    override fun handlerBackPressed() {
        super.handlerBackPressed()
        closeFragment(this)
    }
}