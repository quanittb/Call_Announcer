package com.mobiai.app.ui.activity

import android.content.Context
import android.content.Intent
import androidx.viewpager.widget.ViewPager
import com.mobiai.R
import com.mobiai.app.adapter.OnBoardingViewPagerAdapter
import com.mobiai.app.ui.fragment.OnBoardingFragment
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.activity.BaseActivity
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.ActivityOnboardingBinding

class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding>() {
    private val TAG: String = OnBoardingActivity::javaClass.name
    private var fragmentList: ArrayList<BaseFragment<*>> = arrayListOf()
    private var currentPosition = 0
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_onboarding
    }

    override fun getViewBinding(): ActivityOnboardingBinding {
        return ActivityOnboardingBinding.inflate(layoutInflater)
    }

    override fun createView() {
        showFullscreen(true)
        fragmentList.add(OnBoardingFragment.newInstance(0))
        fragmentList.add(OnBoardingFragment.newInstance(1))
        fragmentList.add(OnBoardingFragment.newInstance(2))
        addControl()

        binding.tvNext.setOnClickListener {
            if (currentPosition != 2) {
                binding.viewPager.currentItem = currentPosition + 1
            } else {
                SharedPreferenceUtils.isCompleteOnboarding = true
                MainActivity.startMain(this, true)
            }
        }
    }

    private fun addControl() {
        val adapter = OnBoardingViewPagerAdapter(
            supportFragmentManager,
            fragmentList,
            this
        )
        binding.viewPager.adapter = adapter
        binding.wormDotsIndicator.attachTo(binding.viewPager)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                currentPosition = position
                if (position == 2) {
                    binding.tvNext.text = getString(R.string._get_started)
                } else {
                    binding.tvNext.text = getString(R.string.next)
                }
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.setPagingEnabled(true)
    }

    companion object {
        fun start(context: Context, clearTask: Boolean = true) {
            Intent(context, OnBoardingActivity::class.java).apply {
                if (clearTask) {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(this)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currentPosition != 0) {
            currentPosition--
            binding.viewPager.currentItem = currentPosition
        } else {
            finish()
        }
    }

}