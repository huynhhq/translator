package com.example.dev00.translator.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dev00.translator.R
import com.example.dev00.translator.models.SpTextData
import kotlinx.android.synthetic.main.item_view.view.*
import java.util.ArrayList


class ListSpeakTextViewAdapter(var items: ArrayList<SpTextData>
                                        , val context: Context) : RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tv_speak_text_left.text = items[position].contentLeft
        holder.itemView.tv_speak_text_right.text = items[position].contentRight
    }

    fun setItemList(items: ArrayList<SpTextData>){
        this.items = items
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvSpeakTextLeft = view.tv_speak_text_left
    val tvSpeakTextRight = view.tv_speak_text_right
}