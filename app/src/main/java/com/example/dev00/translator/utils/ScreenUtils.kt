package com.example.dev00.translator.utils

import android.content.Context
import android.util.TypedValue



class ScreenUtils {
    companion object {
        fun dp2px(context: Context, dp: Int): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                    , dp.toFloat()
                    , context.getResources().getDisplayMetrics()).toInt()
        }
    }
}