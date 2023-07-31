package com.mobiai.app.ui.dialog

import android.content.Context
import com.mobiai.base.basecode.ui.dialog.BaseDialog
import com.mobiai.databinding.DialogGotosettingBinding

class GotosettingDialog(context : Context, callback :  () -> Unit) : BaseDialog(context) {
    val binding = DialogGotosettingBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.cvAllow.setOnClickListener {
            callback()
            dismiss()
        }
    }
}