package com.mobiai.base.basecode.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.mobiai.R
import com.mobiai.base.basecode.language.LanguageUtil

//abstract class BaseDialog<T : ViewBinding>(context: Context) : Dialog(context, R.style.Theme_Dialog) {
//
//    protected lateinit var binding: T
//
//    protected abstract fun getViewBinding(): T
//
//    init{
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = getViewBinding()
//        setContentView(binding.root)
//        setCancelable(true)
//        setup()
//    }
//
//    protected abstract fun setup()
//}

open class BaseDialog(context: Context) : Dialog(context, R.style.Theme_Dialog) {
    init {
        LanguageUtil.setupLanguage(context)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    override fun show() {
        super.show()
//        CheckAdsResume.isDialogOpen = true
    }

    override fun dismiss() {
        super.dismiss()
//        CheckAdsResume.isDialogOpen = false
    }
    protected fun showKeyBoard(){
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
