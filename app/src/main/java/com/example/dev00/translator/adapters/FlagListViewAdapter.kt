package com.example.dev00.translator.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.dev00.translator.R
import com.example.dev00.translator.models.Flag
import com.example.dev00.translator.utils.Utils

class FlagListViewAdapter(var context: Context
                          , var flags: List<Flag>): BaseAdapter() {

    var flagImg: ImageView? = null
    var nameTv: TextView? = null
    var selected_position = flags.size -1

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
        var convertView = view
        if (convertView == null) {
            var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.custom_flags_listview, viewGroup,false)
        }

        flagImg = convertView!!.findViewById(R.id.flag_img) as ImageView
        nameTv = convertView.findViewById(R.id.country_name)

        var temp: Flag = flags.get(i)
        flagImg!!.setImageBitmap(Utils.getBitMapFromAssets(context, temp.image))
        nameTv!!.text = temp.name
        nameTv!!.setTextColor(ContextCompat.getColor(context ,R.color.md_white_1000))
        if(selected_position == i){
            nameTv!!.setTextColor(ContextCompat.getColor(context ,R.color.md_amber_900))
        }
        return convertView
    }

    override fun getItem(position: Int): Any {
        return flags.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return flags.size
    }

    fun setSelectedPosition(pos: Int){

        selected_position = pos
    }

}