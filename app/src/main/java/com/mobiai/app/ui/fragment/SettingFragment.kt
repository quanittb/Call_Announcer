package com.mobiai.app.ui.fragment

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.*
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.mobiai.app.ui.activity.LanguageActivity
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.base.basecode.ads.WrapAdsResume
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentSettingAccountBinding
import java.text.DecimalFormat
import kotlin.math.round
import kotlin.math.roundToInt

class SettingFragment : BaseFragment<FragmentSettingAccountBinding>() {
    companion object {
        fun instance(): SettingFragment {
            return newInstance(SettingFragment::class.java)
        }
    }

    override fun initView() {

        binding.sbSpeakingSpeed.setIndicatorTextDecimalFormat("0")
        binding.sbSpeakingSpeed.setIndicatorTextStringFormat("%s%%")
        binding.sbSpeakingSpeed.setProgress(0f, 100.0f)
        binding.sbSpeakingSpeed.setProgress(SharedPreferenceUtils.speedSpeak.toFloat())
        binding.sbVolume.setIndicatorTextDecimalFormat("0")
        binding.sbVolume.setIndicatorTextStringFormat("%s%%")
        binding.sbVolume.setProgress(0f, 100.0f)
        binding.sbVolume.setProgress(SharedPreferenceUtils.volumeAnnouncer.toFloat())
        binding.lnRingtone.setOnSafeClickListener(500) {
            addFragment(RingstoneFragment.instance())
        }
        binding.lnLanguage.setOnSafeClickListener(500) {
            LanguageActivity.start(requireContext(), true, clearTask = false)
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
            addFragment(SecurityPolicyFragment.instance())
        }

        binding.icArrowLeft.setOnClickListener {
            handlerBackPressed()
        }

        binding.sbVolume.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                rangeSeekBar: RangeSeekBar,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                SharedPreferenceUtils.volumeAnnouncer = round(leftValue).roundToInt()
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }
        })

        binding.sbSpeakingSpeed.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                rangeSeekBar: RangeSeekBar,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                SharedPreferenceUtils.speedSpeak = round(leftValue).roundToInt()
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }
        })
    }

    override fun handlerBackPressed() {
        super.handlerBackPressed()
        closeFragment(this)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingAccountBinding =
        FragmentSettingAccountBinding.inflate(inflater, container, false)
}