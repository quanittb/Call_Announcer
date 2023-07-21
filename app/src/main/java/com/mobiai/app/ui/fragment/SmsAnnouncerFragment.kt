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
import com.mobiai.databinding.FragmentSmsAnnouncerBinding

class SmsAnnouncerFragment :BaseFragment<FragmentSmsAnnouncerBinding>(){

    companion object{
        fun instance() : SmsAnnouncerFragment{
            return newInstance(SmsAnnouncerFragment::class.java)
        }
    }

    private var currentTurn = false
    private var currentToggle1 = false
    private var currentToggle2 = false
    private var currentToggle3 = false
    private var currentToggle4 = false
    private var currentToggle5 = false
    private var currentToggle6 = false
    override fun initView() {

        if (SharedPreferenceUtils.isTurnOnSms ){
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

    private fun turnOn(){
        if (!currentTurn){
            showDialogTurnOn()
        }
        else{
            disableView(false)

        }
    }

    private fun changeToggle(view: ImageView){
        when(view){
            binding.ivToggle1 -> {
                if (!currentToggle1){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    currentToggle1 = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    currentToggle1 = false
                }
            }

            binding.ivToggle2 -> {
                if (!currentToggle2){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    currentToggle2 = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    currentToggle2 = false
                }
            }

            binding.ivToggle3 -> {
                if (!currentToggle3){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    currentToggle3 = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    currentToggle3 = false
                }
            }
            binding.ivToggle4 -> {
                if (!currentToggle4){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    currentToggle4 = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    currentToggle4 = false
                }
            }

            binding.ivToggle5 -> {
                if (!currentToggle5){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    currentToggle5 = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    currentToggle5 = false
                }
            }

            binding.ivToggle6 -> {
                if (!currentToggle6){
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))
                    currentToggle6 = true
                }
                else{
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
                    currentToggle6 = false
                }
            }
        }
     }
    private fun disableView(boolean: Boolean){
        if (!boolean){
            currentTurn = false
            binding.frDisable.visible()
            binding.btnTurn.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on)
            binding.btnTurn.setTextColor(resources.getColor(R.color.color_text_turn))
            SharedPreferenceUtils.isTurnOnSms = false
        }
        else{
            currentTurn = true
            binding.frDisable.gone()
            binding.btnTurn.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on_click)
            binding.btnTurn.setTextColor(resources.getColor(R.color.white))
            SharedPreferenceUtils.isTurnOnSms = true
        }
    }
    private fun showDialogTurnOn(){
        val turnOnDialog = TurnOnDialog(requireContext()){
            disableView(true)
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
    ): FragmentSmsAnnouncerBinding = FragmentSmsAnnouncerBinding.inflate(inflater,container,false)
}