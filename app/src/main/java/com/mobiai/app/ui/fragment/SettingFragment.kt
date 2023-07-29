package com.mobiai.app.ui.fragment

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ads.control.admob.AppOpenManager
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.mobiai.R
import com.mobiai.app.ui.activity.LanguageActivity
import com.mobiai.app.ui.dialog.RequestWriteSettingDialog
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.base.basecode.ads.WrapAdsResume
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentSettingAccountBinding
import kotlin.math.round
import kotlin.math.roundToInt

class SettingFragment : BaseFragment<FragmentSettingAccountBinding>() {
    companion object {
        const val LINK_POLICY ="https://sites.google.com/view/caller-name-announcer-policy/home"
        const val LINK_TERM ="https://sites.google.com/view/caller-name-announcer-tos/home"
        fun instance(): SettingFragment {
            return newInstance(SettingFragment::class.java)
        }
    }

    private lateinit var requestWriteSettingDialog: RequestWriteSettingDialog
    private val DEFAULT_WRITE_SETTING_CODE: Int = 1111

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
            if (!Settings.System.canWrite(requireContext())) {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                requestWriteSettingPermission(DEFAULT_WRITE_SETTING_CODE)
            }
            else{
                addFragment(RingtoneFragment.instance())
            }
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
        binding.lnTerm.setOnSafeClickListener(500) {
//            addFragment(SecurityPolicyFragment.instance())
            WrapAdsResume.instance.disableAdsResumeByClickAction()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(LINK_TERM))
            startActivity(browserIntent)

        }
        binding.lnPolicy.setOnSafeClickListener(200L) {
            // todo policy
            WrapAdsResume.instance.disableAdsResumeByClickAction()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(LINK_POLICY))
            startActivity(browserIntent)
        }

        binding.icArrowLeft.setOnSafeClickListener(500){
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

        binding.sbBatteryMin.setProgress(SharedPreferenceUtils.batteryMin.toFloat())
        binding.sbBatteryMin.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                rangeSeekBar: RangeSeekBar,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                SharedPreferenceUtils.batteryMin = round(leftValue).roundToInt()
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }
        })
    }

    private fun requestWriteSettingPermission(code: Int) {
        requestWriteSettingDialog = RequestWriteSettingDialog(
            requireContext()
        ) {
            openWriteSettingInDevice(code)
        }
        if (!requestWriteSettingDialog.isShowing) {
            requestWriteSettingDialog.show()
        }
    }
    private fun openWriteSettingInDevice(code: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + requireContext().packageName)
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DEFAULT_WRITE_SETTING_CODE) {
            if (Settings.System.canWrite(requireContext())) {
                addFragment(RingtoneFragment.instance())
            }
        }
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