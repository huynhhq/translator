package com.example.dev00.translator.helpers

import android.support.v4.view.ViewCompat
import android.view.View

class ViewHelper {
    companion object{
        fun clear(v: View) {
            ViewCompat.setAlpha(v, 1f)
            ViewCompat.setScaleY(v, 1f)
            ViewCompat.setScaleX(v, 1f)
            ViewCompat.setTranslationY(v, 0f)
            ViewCompat.setTranslationX(v, 0f)
            ViewCompat.setRotation(v, 0f)
            ViewCompat.setRotationY(v, 0f)
            ViewCompat.setRotationX(v, 0f)
            ViewCompat.setPivotY(v, v.measuredHeight / 2f)
            ViewCompat.setPivotX(v, v.measuredWidth / 2f)
            ViewCompat.animate(v).setInterpolator(null).setStartDelay(0)
        }
    }
}