package com.mobiai.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.mobiai.R
import com.mobiai.app.ui.model.ItemDeviceRingtone
import com.mobiai.base.basecode.adapter.BaseAdapter
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.databinding.ItemRingtoneBinding

class MyRingtoneAdapter(val context : Context, val listener : OnRingtoneClickListener) : BaseAdapter<ItemDeviceRingtone, ItemRingtoneBinding>() {

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemRingtoneBinding {
        return ItemRingtoneBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemRingtoneBinding, item: ItemDeviceRingtone, position: Int) {
        binding.txtName.text = item.name
        if(item.isChoose){
            binding.icSelect.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_select_language))
        }else{
            binding.icSelect.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_un_select_lang))
        }

        binding.root.setOnClickListener {
            listener.onClickItemListener(item)
            for (i in listItem.indices) {
                listItem[i].isChoose = i == listItem.indexOf(item)
            }
            notifyDataSetChanged()
        }

    }
    fun updateItemRingtonePlayingState(updateItem: ItemDeviceRingtone) {
        if (listItem.isEmpty()){
            return
        }
        for (item in listItem){

            if (item == updateItem){
                item.isPlaying = updateItem.isPlaying
            }else{
                item.isPlaying = false
            }
        }
        notifyDataSetChanged()
    }
    fun updateSelectedItem() {

        if (listItem.isEmpty()) {
            return
        }
        val saved_ringtone_id = SharedPreferenceUtils.saved_ringtone_id
        Log.i("TAG", "updateSelectedItem: ")
        if (listItem.size == 1) {
            listItem[0].isChoose = listItem[0].id == saved_ringtone_id
            notifyDataSetChanged()
        } else {
            for (item in listItem){
                item.isChoose = item.id == saved_ringtone_id
            }
            notifyDataSetChanged()
        }
    }

    interface OnRingtoneClickListener {
        fun onClickItemListener(item: ItemDeviceRingtone)
    }
}