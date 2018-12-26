package com.example.dev00.translator.activities

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.dev00.translator.R
import kotlinx.android.synthetic.main.activity_loading.*
import java.lang.Exception
import com.example.dev00.translator.utils.Utils
import android.view.ViewGroup
import android.widget.LinearLayout



class LoadingActivity : AppCompatActivity() {

    private var tag = "LoadingActivity"
    private var animation: Animation? = null
    private var mHandler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            var intent = Intent()
            intent.setClass(this@LoadingActivity, MainActivity::class.java)
            startActivity(intent)
            this@LoadingActivity.finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_loading)
            var window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
                val titleBarHeight = getStatusBarHeight()
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleBarHeight)
                statusbar.setLayoutParams(params)
                statusbar.setVisibility(View.VISIBLE)
            }else{
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.md_grey_100))
            }
            animation = AnimationUtils.loadAnimation(this, R.anim.from_bottom)
            ivDown.startAnimation(animation)
            animation = AnimationUtils.loadAnimation(this, R.anim.from_top)
            ivUp.startAnimation(animation)
            mHandler.sendEmptyMessageDelayed(1, 3500)
        } catch (e: Exception) {
            Utils.writeLog(tag, e.message)
        }
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}
