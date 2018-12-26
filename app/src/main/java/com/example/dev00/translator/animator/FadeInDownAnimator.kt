package com.example.dev00.translator.animator

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.animation.Interpolator

class FadeInDownAnimator(var interpolator: Interpolator?): BaseItemAnimator() {

    override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder) {
        ViewCompat.setTranslationY(holder.itemView, -holder.itemView.height * .25f)
        ViewCompat.setAlpha(holder.itemView, 0f)
    }

    override fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        ViewCompat.animate(holder.itemView)
                .translationY(-holder.itemView.height * .25f)
                .alpha(0f)
                .setDuration(removeDuration)
                .setInterpolator(mInterpolator)
                .setListener(DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start()
    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        ViewCompat.animate(holder.itemView)
                .translationY(0f)
                .alpha(1f)
                .setDuration(addDuration)
                .setInterpolator(mInterpolator)
                .setListener(DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start()
    }

}