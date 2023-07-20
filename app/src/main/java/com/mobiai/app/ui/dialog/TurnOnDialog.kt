package com.mobiai.app.ui.dialog

import android.content.Context
import com.mobiai.base.basecode.ui.dialog.BaseDialog
import com.mobiai.databinding.DialogChangeTurnOnBinding

class TurnOnDialog(context : Context, callback :  () -> Unit) :BaseDialog(context) {
    val binding = DialogChangeTurnOnBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.cvAllow.setOnClickListener {
            callback()
            dismiss()
        }
    }
}