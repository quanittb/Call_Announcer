package com.mobiai.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mobiai.R
import com.mobiai.base.basecode.extensions.invisible
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentOnboardingViewBinding

class OnBoardingFragment : BaseFragment<FragmentOnboardingViewBinding>() {
    companion object {
        private const val TAG = "OnBoardingFragment"
        private const val ON_BOARDING_POSITION: String = "on_boarding_position"
        private var pagePosition = 0

        fun newInstance(position: Int): OnBoardingFragment {
            val fragment = OnBoardingFragment()
            val bundle = Bundle()
            bundle.putInt(ON_BOARDING_POSITION, position)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        pagePosition = requireArguments().getInt(ON_BOARDING_POSITION)
        setupViews()
    }

    private fun setupViews(){
        when(pagePosition) {
            0 -> {
                binding.ctlOnboarding1.visible()
                binding.ctlOnboarding2.invisible()
                binding.ctlOnboarding3.invisible()
                binding.tvTitle.text = getString(R.string._onboarding_1_title)
                binding.tvDes.text = getString(R.string._onboarding_1_description)
            }
            1 -> {
                binding.ctlOnboarding2.visible()
                binding.ctlOnboarding1.invisible()
                binding.ctlOnboarding3.invisible()
                binding.tvTitle.text = getString(R.string._onboarding_2_title)
                binding.tvDes.text = getString(R.string._onboarding_2_description)
            }
            2 -> {
                binding.ctlOnboarding3.visible()
                binding.ctlOnboarding1.invisible()
                binding.ctlOnboarding2.invisible()
                binding.tvTitle.text = getString(R.string._onboarding_3_title)
                binding.tvDes.text = getString(R.string._onboarding_3_description)
            }
        }
    }
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnboardingViewBinding = FragmentOnboardingViewBinding.inflate(inflater, container, false)
}