package com.example.dev00.translator.interfaces

import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.RecyclerView

interface AnimateViewHolder {
    abstract fun preAnimateAddImpl(holder: RecyclerView.ViewHolder)

    abstract fun preAnimateRemoveImpl(holder: RecyclerView.ViewHolder)

    abstract fun animateAddImpl(holder: RecyclerView.ViewHolder, listener: ViewPropertyAnimatorListener)

    abstract fun animateRemoveImpl(holder: RecyclerView.ViewHolder,
                                   listener: ViewPropertyAnimatorListener)
}