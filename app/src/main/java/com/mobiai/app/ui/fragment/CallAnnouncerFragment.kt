package com.mobiai.app.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.mobiai.R
import com.mobiai.app.ui.dialog.TurnOnDialog
import com.mobiai.base.basecode.extensions.gone
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentCallAnnouncerBinding

class CallAnnouncerFragment : BaseFragment<FragmentCallAnnouncerBinding>() {

    companion object {
        fun instance(): CallAnnouncerFragment {
            return newInstance(CallAnnouncerFragment::class.java)
        }
    }

    private var currentTurn = SharedPreferenceUtils.isTurnOnAnnouncer
    private var currentToggle1 = SharedPreferenceUtils.isTurnOnModeNormal
    private var currentToggle2 = SharedPreferenceUtils.isTurnOnModeSilent
    private var currentToggle3 = SharedPreferenceUtils.isTurnOnModeVibrate
    private var currentToggle4 = SharedPreferenceUtils.isTurnOnFlash
    private var currentToggle5 = SharedPreferenceUtils.isUnknownNumber
    private var currentToggle6 = SharedPreferenceUtils.isReadName
    override fun initView() {
        checkStatus()
        if (SharedPreferenceUtils.isTurnOnCall) {
            disableView(true)
        }
        binding.icBack.setOnClickListener {
            handlerBackPressed()
        }

        binding.btnTurn.setOnClickListener {
            turnOn()
        }
        binding.ivToggle1.setOnClickListener {
            changeToggle(binding.ivToggle1)
        }
        binding.ivToggle2.setOnClickListener {
            changeToggle(binding.ivToggle2)
        }

        binding.ivToggle3.setOnClickListener {
            changeToggle(binding.ivToggle3)
        }
        binding.ivToggle4.setOnClickListener {
            changeToggle(binding.ivToggle4)
        }

        binding.ivToggle5.setOnClickListener {
            changeToggle(binding.ivToggle5)
        }

        binding.ivToggle6.setOnClickListener {
            changeToggle(binding.ivToggle6)
        }
    }

    private fun checkStatus() {
        currentTurn = !SharedPreferenceUtils.isTurnOnAnnouncer
        currentToggle1 = !SharedPreferenceUtils.isTurnOnModeNormal
        currentToggle2 = !SharedPreferenceUtils.isTurnOnModeSilent
        currentToggle3 = !SharedPreferenceUtils.isTurnOnModeVibrate
        currentToggle4 = !SharedPreferenceUtils.isTurnOnFlash
        currentToggle5 = !SharedPreferenceUtils.isUnknownNumber
        currentToggle6 = !SharedPreferenceUtils.isReadName
        changeToggle(binding.ivToggle1)
        changeToggle(binding.ivToggle2)
        changeToggle(binding.ivToggle3)
        changeToggle(binding.ivToggle4)
        changeToggle(binding.ivToggle5)
        changeToggle(binding.ivToggle6)
    }

    private fun turnOfAnnouncer() {
        currentToggle1 = true
        currentToggle2 = true
        currentToggle3 = true
        currentToggle4 = true
        currentToggle5 = true
        currentToggle6 = true
        changeToggle(binding.ivToggle1)
        changeToggle(binding.ivToggle2)
        changeToggle(binding.ivToggle3)
        changeToggle(binding.ivToggle4)
        changeToggle(binding.ivToggle5)
        changeToggle(binding.ivToggle6)
        SharedPreferenceUtils.isTurnOnModeNormal = currentToggle1
        SharedPreferenceUtils.isTurnOnModeSilent = currentToggle2
        SharedPreferenceUtils.isTurnOnModeVibrate = currentToggle3
        SharedPreferenceUtils.isTurnOnFlash = currentToggle4
        SharedPreferenceUtils.isUnknownNumber = currentToggle5
        SharedPreferenceUtils.isReadName = currentToggle6
    }

    private fun turnOnAnnouncer() {
        currentToggle1 = false
        currentToggle2 = true
        currentToggle3 = true
        currentToggle4 = true
        currentToggle5 = false
        currentToggle6 = false
        changeToggle(binding.ivToggle1)
        changeToggle(binding.ivToggle2)
        changeToggle(binding.ivToggle3)
        changeToggle(binding.ivToggle4)
        changeToggle(binding.ivToggle5)
        changeToggle(binding.ivToggle6)
        SharedPreferenceUtils.isTurnOnModeNormal = currentToggle1
        SharedPreferenceUtils.isTurnOnModeSilent = currentToggle2
        SharedPreferenceUtils.isTurnOnModeVibrate = currentToggle3
        SharedPreferenceUtils.isTurnOnFlash = currentToggle4
        SharedPreferenceUtils.isUnknownNumber = currentToggle5
        SharedPreferenceUtils.isReadName = currentToggle6
    }

    private fun turnOn() {
        if (!currentTurn) {
            showDialogTurnOn()
            SharedPreferenceUtils.isTurnOnAnnouncer = true
        } else {
            turnOfAnnouncer()
            disableView(false)
            SharedPreferenceUtils.isTurnOnAnnouncer = false
        }
    }

    private fun disableView(boolean: Boolean) {
        if (!boolean) {
            currentTurn = false
            binding.frDisable.visible()
            binding.btnTurn.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on)
            binding.btnTurn.setTextColor(resources.getColor(R.color.color_text_turn))
            SharedPreferenceUtils.isTurnOnCall = false
        } else {
            currentTurn = true
            binding.frDisable.gone()
            binding.btnTurn.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on_click)
            binding.btnTurn.setTextColor(resources.getColor(R.color.white))
            SharedPreferenceUtils.isTurnOnCall = true
        }
    }

    private fun changeToggle(view: ImageView) {
        when (view) {
            binding.ivToggle1 -> {
                if (!currentToggle1) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    currentToggle1 = true
                    SharedPreferenceUtils.isTurnOnModeNormal = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    currentToggle1 = false
                    SharedPreferenceUtils.isTurnOnModeNormal = false

                }
            }

            binding.ivToggle2 -> {
                if (!currentToggle2) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    currentToggle2 = true
                    SharedPreferenceUtils.isTurnOnModeSilent = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    currentToggle2 = false
                    SharedPreferenceUtils.isTurnOnModeSilent = false
                }
            }

            binding.ivToggle3 -> {
                if (!currentToggle3) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    currentToggle3 = true
                    SharedPreferenceUtils.isTurnOnModeVibrate = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    currentToggle3 = false
                    SharedPreferenceUtils.isTurnOnModeVibrate = false
                }
            }

            binding.ivToggle4 -> {
                if (!currentToggle4) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    currentToggle4 = true
                    SharedPreferenceUtils.isTurnOnFlash = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    currentToggle4 = false
                    SharedPreferenceUtils.isTurnOnFlash = false
                }
            }

            binding.ivToggle5 -> {
                if (!currentToggle5) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    currentToggle5 = true
                    SharedPreferenceUtils.isUnknownNumber = currentToggle5
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    currentToggle5 = false
                    SharedPreferenceUtils.isUnknownNumber = currentToggle5
                }
            }

            binding.ivToggle6 -> {
                if (!currentToggle6) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    currentToggle6 = true
                    SharedPreferenceUtils.isReadName = currentToggle6
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    currentToggle6 = false
                    SharedPreferenceUtils.isReadName = currentToggle6
                }
            }
        }

    }

    private fun showDialogTurnOn() {
        val turnOnDialog = TurnOnDialog(requireContext()) {
            disableView(true)
            turnOnAnnouncer()
        }
        turnOnDialog.show()
    }

    override fun handlerBackPressed() {
        super.handlerBackPressed()
        closeFragment(this)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCallAnnouncerBinding =
        FragmentCallAnnouncerBinding.inflate(inflater, container, false)

    private fun checkModeNotification() {

    }
}