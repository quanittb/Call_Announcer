package com.mobiai.app.ui.dialog

import android.content.Context
import com.mobiai.base.basecode.ui.dialog.BaseDialog
import com.mobiai.databinding.DialogRequestWriteSettingBinding

class RequestWriteSettingDialog(context: Context, callback : () -> Unit) : BaseDialog(context){

    val binding = DialogRequestWriteSettingBinding.inflate(layoutInflater)
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