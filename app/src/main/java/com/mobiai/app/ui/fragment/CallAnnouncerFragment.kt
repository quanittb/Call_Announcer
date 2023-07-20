package com.mobiai.app.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.mobiai.R
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentCallAnnouncerBinding

class CallAnnouncerFragment :BaseFragment<FragmentCallAnnouncerBinding>(){

    companion object{
        fun instance() : CallAnnouncerFragment{
            return newInstance(CallAnnouncerFragment::class.java)
        }
    }

    private var currentTurn = false
    private var currentToggle = false
    override fun initView() {
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
            currentTurn = true
            binding.btnTurn.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on_click)
            binding.btnTurn.setTextColor(resources.getColor(R.color.white))
        }
        else{
            currentTurn = false
            binding.btnTurn.background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_turn_on)
            binding.btnTurn.setTextColor(resources.getColor(R.color.color_text_turn))
        }
    }

    private fun changeToggle(view: ImageView){
        if (!currentToggle){
            currentToggle = true
            view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_on))

        }
        else{
            currentToggle = false
            view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_togle_off))
        }
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