package com.mobiai.app.ui.dialog

import android.content.Context
import com.mobiai.R
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
    fun gotoSetingNotify(){
        binding.txtTitle.text = context.getString(R.string.you_need_permisison_notify_access)
    }

    fun gotoSetingCallerID(){
        binding.txtTitle.text = context.getString(R.string.you_need_permisison_caller_id_and_spam_app)
    }
    fun gotoSetingContact(){
        binding.txtTitle.text = context.getString(R.string.you_need_permisison_contact)
    }

    fun gotoSetingAudio(){
        binding.txtTitle.text = context.getString(R.string.you_need_permisison_audio)
    }
}