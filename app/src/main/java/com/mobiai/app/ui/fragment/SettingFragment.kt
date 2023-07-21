package com.mobiai.app.ui.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jaygoo.widget.R
import com.mobiai.app.ui.activity.LanguageActivity
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.base.basecode.ads.WrapAdsResume
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

        binding.lnRingtone.setOnSafeClickListener(500) {
            addFragment(RingstoneFragment.instance())
        }
        binding.lnLanguage.setOnSafeClickListener(500) {
            LanguageActivity.start(requireContext(),true, clearTask = false)
        }
        binding.icArrowLeft.setOnSafeClickListener(500) {
            handlerBackPressed()
        }

        binding.lnShare.setOnSafeClickListener(500) {
            val appLink =
                "https://play.google.com/store/apps/details?id=" + requireContext().packageName
            WrapAdsResume.instance.disableAdsResumeByClickAction()
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, appLink)
            val chooserIntent = Intent.createChooser(shareIntent, "Share ")
            if (chooserIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(chooserIntent)
            }
        }
        binding.lnTermAndPolicy.setOnSafeClickListener(500) {
            //todo
        }

        binding.icArrowLeft.setOnClickListener{
            handlerBackPressed()
        }
        binding.privacy.setOnClickListener{
            addFragment(SecurityPolicyFragment.instance())
        }
    }
    override fun handlerBackPressed() {
        super.handlerBackPressed()
        closeFragment(this)
    }

    override fun handlerBackPressed() {
        super.handlerBackPressed()
        closeFragment(this)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingAccountBinding = FragmentSettingAccountBinding.inflate(inflater,container,false)
}