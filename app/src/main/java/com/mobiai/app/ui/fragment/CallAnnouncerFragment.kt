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

class CallAnnouncerFragment :BaseFragment<FragmentCallAnnouncerBinding>(){

    companion object{
        fun instance() : CallAnnouncerFragment{
            return newInstance(CallAnnouncerFragment::class.java)
        }
    }

    override fun initView() {
        checkStatus()

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

    private fun checkStatus(){
        if (SharedPreferenceUtils.isTurnOnCall){
            disableView(true)
            checkToggle()
        }
        else{
            binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
        }
    }

    private fun turnOn() {
        if (!SharedPreferenceUtils.isTurnOnCall) {
            showDialogTurnOn()
        } else {
            disableView(false)
            changeAllToggle(false)
        }
    }

    private fun disableView(boolean: Boolean) {
        if (!boolean) {
            binding.frDisable.visible()
            binding.btnTurn.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on)
            binding.btnTurn.setTextColor(resources.getColor(R.color.color_text_turn))
            SharedPreferenceUtils.isTurnOnCall = false
        } else {
            binding.frDisable.gone()
            binding.btnTurn.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on_click)
            binding.btnTurn.setTextColor(resources.getColor(R.color.white))
            SharedPreferenceUtils.isTurnOnCall = true
        }
    }

    private fun changeAllToggle(boolean: Boolean){
        if (boolean){
            binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
            binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
            changeSharePreference(true)
        }
        else{
            binding.ivToggle1.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle2.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle4.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle5.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            binding.ivToggle6.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off_all))
            changeSharePreference(false)
        }
    }

    private fun changeSharePreference(boolean: Boolean){
        if (boolean){
            SharedPreferenceUtils.isTurnOnModeNormal = true
            SharedPreferenceUtils.isTurnOnModeSilent = false
            SharedPreferenceUtils.isTurnOnModeVibrate = false
            SharedPreferenceUtils.isTurnOnFlash = false
            SharedPreferenceUtils.isUnknownNumber = true
            SharedPreferenceUtils.isReadName = true
        }
        else{
            SharedPreferenceUtils.isTurnOnModeNormal = false
            SharedPreferenceUtils.isTurnOnModeSilent = false
            SharedPreferenceUtils.isTurnOnModeVibrate = false
            SharedPreferenceUtils.isTurnOnFlash = false
            SharedPreferenceUtils.isUnknownNumber = false
            SharedPreferenceUtils.isReadName = false
        }
    }

    private fun changeToggle(view: ImageView) {
        when (view) {
            binding.ivToggle1 -> {
                if (!SharedPreferenceUtils.isTurnOnModeNormal) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeNormal = true
                }
                else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeNormal = false

                }
            }

            binding.ivToggle2 -> {
                if (!SharedPreferenceUtils.isTurnOnModeSilent) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeSilent = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeSilent = false
                }
            }

            binding.ivToggle3 -> {
                if (!SharedPreferenceUtils.isTurnOnModeVibrate) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeVibrate = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isTurnOnModeVibrate = false
                }
            }

            binding.ivToggle4 -> {
                if (!SharedPreferenceUtils.isTurnOnFlash) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isTurnOnFlash = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isTurnOnFlash = false
                }
            }

            binding.ivToggle5 -> {
                if (!SharedPreferenceUtils.isUnknownNumber) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isUnknownNumber = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isUnknownNumber = false
                }
            }

            binding.ivToggle6 -> {
                if (!SharedPreferenceUtils.isReadName) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_on
                        )
                    )
                    SharedPreferenceUtils.isReadName = true
                } else {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_togle_off
                        )
                    )
                    SharedPreferenceUtils.isReadName = false
                }
            }
        }
    }
    private fun checkToggle() {
        if (SharedPreferenceUtils.isTurnOnModeNormal) {
            binding.ivToggle1.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle1.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }
        if (SharedPreferenceUtils.isTurnOnModeSilent) {
            binding.ivToggle2.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle2.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }

        if (SharedPreferenceUtils.isTurnOnModeVibrate) {
            binding.ivToggle3.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle3.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }

        if (SharedPreferenceUtils.isTurnOnFlash) {
            binding.ivToggle4.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle4.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }

        if (SharedPreferenceUtils.isUnknownNumber) {
            binding.ivToggle5.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle5.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }

        if (SharedPreferenceUtils.isReadName) {
            binding.ivToggle6.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_on
                )
            )
        }
        else{
            binding.ivToggle6.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_togle_off
                )
            )
        }
    }

    private fun showDialogTurnOn(){
       val turnOnDialog = TurnOnDialog(requireContext()){
           disableView(true)
           changeAllToggle(true)
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
    ): FragmentCallAnnouncerBinding = FragmentCallAnnouncerBinding.inflate(inflater,container,false)
}