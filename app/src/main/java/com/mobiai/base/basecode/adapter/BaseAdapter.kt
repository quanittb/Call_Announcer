package com.mobiai.base.basecode.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VB : ViewBinding> : RecyclerView.Adapter<BaseAdapter<T, VB>.ViewHolder>() {

    protected val listItem =  mutableListOf<T>()
    private var binding : VB? = null
    var itemSelectedPosition : Int  = RecyclerView.NO_POSITION

    abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup) : VB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =  LayoutInflater.from(parent.context)
        binding = createBinding(inflater, parent)
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItem[position]
        bind(holder.binding, item, position)
    }

    override fun getItemCount(): Int = listItem.size

    abstract fun bind(binding: VB, item: T, position: Int)

     fun setItems(items: List<T>) {
        this.listItem.clear()
        this.listItem.addAll(items)
        notifyDataSetChanged()
    }
    fun removeItem(position: Int) {
        if (position >= 0) {
            listItem.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun addItems(item: List<T>) {
        this.listItem.addAll(item)
        notifyDataSetChanged()
    }
    fun addItem(item: T, index : Int ) {
        this.listItem.add(index, item)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}