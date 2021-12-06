package com.martynov.testmapyandex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_count.view.*

class Adapter : RecyclerView.Adapter<VH>() {
    val list = arrayListOf<String>()


    fun add(newList: List<UserDataTemplase>){
        for(i in newList){
            list.add(i.text)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_count, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
       holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class VH(private val view: View): RecyclerView.ViewHolder(view) {
    fun bind(text:String){
        view.textView.text = text
    }

}
