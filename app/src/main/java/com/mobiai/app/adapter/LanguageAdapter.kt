package com.mobiai.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.mobiai.R
import com.mobiai.base.basecode.adapter.BaseAdapter
import com.mobiai.base.basecode.language.Language
import com.mobiai.databinding.ItemLanguageBinding

class LanguageAdapter(val context : Context, val listener : OnLanguageClickListener) : BaseAdapter<Language, ItemLanguageBinding>() {

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemLanguageBinding {
        return ItemLanguageBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemLanguageBinding, item: Language, position: Int) {
        binding.txtNameLanguage.text = item.title
        binding.imgIconLanguage.setImageDrawable(AppCompatResources.getDrawable(context, item.flag))
        if(item.isChoose){
            binding.imgChooseLanguage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_select_language))
        }else{
            binding.imgChooseLanguage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_un_select_lang))
        }
        binding.root.setOnClickListener {
            listener.onClickItemListener(item)
            for (i in listItem.indices) {
                listItem[i].isChoose = i == listItem.indexOf(item)
            }
            notifyDataSetChanged()
        }
    }


    interface OnLanguageClickListener {
        fun onClickItemListener(language: Language?)
    }
}